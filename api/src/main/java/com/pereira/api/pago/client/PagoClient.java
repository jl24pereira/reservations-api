package com.pereira.api.pago.client;

import com.pereira.api.pago.dto.ValidacionPagoRequest;
import com.pereira.api.pago.dto.ValidacionPagoResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Component
@AllArgsConstructor
public class PagoClient {

    private final RestClient restClient;

    public ValidacionPagoResponse validate(ValidacionPagoRequest request) {
        return restClient.post()
                .uri("/payments/validate")
                .body(request)
                .retrieve()
                .body(ValidacionPagoResponse.class);
    }

}
