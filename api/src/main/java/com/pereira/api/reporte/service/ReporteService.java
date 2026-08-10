package com.pereira.api.reporte.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import com.pereira.api.reporte.dto.OcupacionEspacioResponse;
import com.pereira.api.reporte.repository.ReporteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class ReporteService {

    private final ReporteRepository repository;

    @Transactional(readOnly = true)
    public List<OcupacionEspacioResponse> ocupacion(OffsetDateTime desde, OffsetDateTime hasta) {
        long horasDisponibles = Duration.between(desde, hasta).toHours();

        return repository.calcularOcupacion(desde, hasta).stream()
                .map(p -> {
                    long reservadas = Math.round(p.getHorasReservadas());
                    BigDecimal porcentaje = horasDisponibles == 0 ? BigDecimal.ZERO
                            : BigDecimal.valueOf(reservadas).multiply(BigDecimal.valueOf(100))
                                    .divide(BigDecimal.valueOf(horasDisponibles), 2, RoundingMode.HALF_UP);

                    return new OcupacionEspacioResponse(p.getEspacioId(), p.getEspacioNombre(), reservadas,
                            horasDisponibles, porcentaje, p.getTotalReservas());
                }).toList();
    }

}
