package com.pereira.api.pago.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.pereira.api.pago.client.PagoClient;
import com.pereira.api.pago.dto.ValidacionPagoRequest;
import com.pereira.api.pago.dto.ValidacionPagoResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoClient client;

    @CircuitBreaker(name = "pago", fallbackMethod = "fallbackValidate")
    public ValidacionPagoResponse validate(UUID reservaId, String paymentMethod, BigDecimal monto) {
        return client.validate(new ValidacionPagoRequest(reservaId, paymentMethod, monto));
    }

    @SuppressWarnings("unused")
    private ValidacionPagoResponse fallbackValidate(UUID reservaId, String paymentMethod, BigDecimal monto,
            Throwable t) {
        log.warn("Validacion de pago no disponible para reserva {} ({}): {}", reservaId, t.getClass().getSimpleName(),
                t.getMessage());
        return ValidacionPagoResponse.pendiente();
    }
}
