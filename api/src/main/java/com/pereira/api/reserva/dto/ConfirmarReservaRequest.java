package com.pereira.api.reserva.dto;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Jose Luis Pereira
 */
public record ConfirmarReservaRequest(@NotBlank String paymentMethod) {

}
