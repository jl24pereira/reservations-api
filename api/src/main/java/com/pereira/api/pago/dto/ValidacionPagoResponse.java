package com.pereira.api.pago.dto;

/**
 *
 * @author Jose Luis Pereira
 */
public record ValidacionPagoResponse(String authorizationId, boolean approved) {
    public static ValidacionPagoResponse pendiente() {
        return new ValidacionPagoResponse(null, false);
    }
}
