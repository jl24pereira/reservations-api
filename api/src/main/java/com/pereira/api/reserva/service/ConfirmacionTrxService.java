package com.pereira.api.reserva.service;

import java.util.UUID;

import com.pereira.api.pago.domain.EstadoPago;
import com.pereira.api.pago.domain.Pago;
import com.pereira.api.pago.dto.ValidacionPagoResponse;
import com.pereira.api.pago.repository.PagoRepository;
import com.pereira.api.reserva.domain.EstadoReserva;
import com.pereira.api.reserva.domain.Reserva;
import com.pereira.api.reserva.dto.ReservaResponse;
import com.pereira.api.reserva.repository.ReservaRepository;
import com.pereira.api.shared.exception.InvalidTransactionException;
import com.pereira.api.shared.exception.ReservaNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class ConfirmacionTrxService {

    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;

    @Transactional(readOnly = true)
    public ConfirmedData prepareConfirmacion(UUID reservaId, UUID usuarioId, boolean isAdmin) {
        Reserva reserva = findWithPermission(reservaId, usuarioId, isAdmin);

        if (reserva.getEstado() != EstadoReserva.PENDING && reserva.getEstado() != EstadoReserva.PENDING_PAYMENT)
            throw new InvalidTransactionException(reserva.getEstado(), "confirmar");

        return new ConfirmedData(reserva.getMontoTotal());
    }

    @Transactional
    public ReservaResponse applyPaymentResult(UUID reservaId, String paymentMethod, ValidacionPagoResponse validacion) {
        Reserva reserva = reservaRepository.findByIdConDetalle(reservaId)
                .orElseThrow(() -> new ReservaNotFoundException(reservaId));

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMetodo(paymentMethod);

        if (validacion.approved()) {
            pago.setEstado(EstadoPago.APPROVED);
            pago.setAutorizacionId(validacion.authorizationId());
            reserva.confirmar();
        } else {
            pago.setEstado(EstadoPago.PENDING);
            reserva.pagoPendiente();
        }

        pagoRepository.save(pago);
        return ReservaResponse.from(reserva);
    }

    private Reserva findWithPermission(UUID reservaId, UUID usuarioId, boolean isAdmin) {
        Reserva reserva = reservaRepository.findByIdConDetalle(reservaId)
                .orElseThrow(() -> new ReservaNotFoundException(reservaId));

        if (!isAdmin && !reserva.getUsuario().getId().equals(usuarioId)) {
            throw new ReservaNotFoundException(reservaId);
        }

        return reserva;
    }
}
