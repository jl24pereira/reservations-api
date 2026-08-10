package com.pereira.api.reporte.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public record OcupacionEspacioResponse(
        UUID espacioId,
        String espacioNombre,
        long horasReservadas,
        long horasDisponibles,
        BigDecimal porcentajeOcupacion,
        long totalReservas) {

}
