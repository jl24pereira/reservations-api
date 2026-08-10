package com.pereira.api.pago.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public record ValidacionPagoRequest(UUID reservaId, String paymentMethod, BigDecimal monto) {

}
