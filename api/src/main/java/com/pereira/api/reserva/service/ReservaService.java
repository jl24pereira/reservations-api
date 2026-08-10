package com.pereira.api.reserva.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.pereira.api.espacio.domain.Espacio;
import com.pereira.api.espacio.repository.EspacioRepository;
import com.pereira.api.reserva.domain.Reserva;
import com.pereira.api.reserva.dto.CreateReservaRequest;
import com.pereira.api.reserva.dto.FilterReservaRequest;
import com.pereira.api.reserva.dto.ReservaResponse;
import com.pereira.api.reserva.repository.ReservaRepository;
import com.pereira.api.shared.exception.EspacioNotFoundException;
import com.pereira.api.shared.exception.InvalidRangeException;
import com.pereira.api.shared.exception.OverReservaException;
import com.pereira.api.shared.exception.ReservaNotFoundException;
import com.pereira.api.usuario.domain.Usuario;
import com.pereira.api.usuario.repository.UsuarioRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EspacioRepository espacioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ReservaResponse create(CreateReservaRequest request, UUID usuarioId) {

        validateRange(request.inicio(), request.fin());

        Espacio espacio = espacioRepository.findById(request.espacioId())
                .orElseThrow(() -> new EspacioNotFoundException(request.espacioId()));

        if (!espacio.isActivo()) {
            throw new InvalidRangeException("El espacio no esta disponible");
        }

        // Chequeo optimista: da un 409 limpio en el caso normal
        if (reservaRepository.overExists(espacio.getId(), request.inicio(), request.fin())) {
            throw new OverReservaException();
        }

        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);

        Reserva reserva = Reserva.createNew(espacio, usuario, request.inicio(), request.fin());
        reserva.setMontoTotal(calculateAmount(espacio.getTarifaHora(), request.inicio(), request.fin()));

        try {
            reservaRepository.saveAndFlush(reserva);
        } catch (DataIntegrityViolationException e) {
            // Red de seguridad: dos peticiones concurrentes que pasaron el chequeo
            if (isOverViolation(e)) {
                throw new OverReservaException();
            }
            throw e;
        }

        return ReservaResponse.from(reserva);
    }

    @Transactional
    public ReservaResponse cancel(UUID reservaId, UUID usuarioId, boolean esAdmin) {
        var reserva = findWithPermissions(reservaId, usuarioId, esAdmin);
        reserva.cancelar();
        return ReservaResponse.from(reserva);
    }

    @Transactional(readOnly = true)
    public ReservaResponse get(UUID reservaId, UUID usuarioId, boolean esAdmin) {
        return ReservaResponse.from(findWithPermissions(reservaId, usuarioId, esAdmin));
    }

    @Transactional(readOnly = true)
    public Page<ReservaResponse> list(FilterReservaRequest filter, Pageable pageable,
            UUID usuarioId, boolean esAdmin) {
        var filtroUsuario = esAdmin ? null : usuarioId;
        return reservaRepository
                .find(filtroUsuario, filter.espacioId(), filter.estado(),
                        filter.desde(), filter.hasta(), pageable)
                .map(ReservaResponse::from);
    }

    private Reserva findWithPermissions(UUID reservaId, UUID usuarioId, boolean esAdmin) {
        var reserva = reservaRepository.findByIdConDetalle(reservaId)
                .orElseThrow(() -> new ReservaNotFoundException(reservaId));

        if (!esAdmin && !reserva.getUsuario().getId().equals(usuarioId)) {
            throw new ReservaNotFoundException(reservaId);
        }
        return reserva;
    }

    private void validateRange(OffsetDateTime inicio, OffsetDateTime fin) {
        if (!fin.isAfter(inicio))
            throw new InvalidRangeException("La hora de fin debe ser posterior a la de inicio");

        if (Duration.between(inicio, fin).toHours() > 12)
            throw new InvalidRangeException("La reserva no puede exceder 12 horas");

    }

    private BigDecimal calculateAmount(BigDecimal tarifaHora, OffsetDateTime inicio, OffsetDateTime fin) {
        var minutos = Duration.between(inicio, fin).toMinutes();
        return tarifaHora
                .multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private boolean isOverViolation(DataIntegrityViolationException e) {
        var mensaje = e.getMostSpecificCause().getMessage();
        return mensaje != null && mensaje.contains("ex_reserva_solapada");
    }

}
