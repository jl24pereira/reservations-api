package com.pereira.api.reserva.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.pereira.api.reserva.domain.EstadoReserva;

/**
 *
 * @author Jose Luis Pereira
 */
public record FilterReservaRequest(
        UUID espacioId,
        EstadoReserva estado,
        OffsetDateTime desde,
        OffsetDateTime hasta) {

}
