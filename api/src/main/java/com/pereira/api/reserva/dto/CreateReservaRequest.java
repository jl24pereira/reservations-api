package com.pereira.api.reserva.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author Jose Luis Pereira
 */
public record CreateReservaRequest(
        @NotNull UUID espacioId,
        @NotNull @Future OffsetDateTime inicio,
        @NotNull @Future OffsetDateTime fin) {

}
