package com.pereira.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import com.pereira.api.pago.client.PagoClient;
import com.pereira.api.pago.dto.ValidacionPagoRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Jose Luis Pereira
 */
@SpringBootTest
public class PagoClientTest {

    @Autowired
    PagoClient client;

    @Test
    void aprobado() {
        var r = client.validate(new ValidacionPagoRequest(UUID.randomUUID(), "VISA", BigDecimal.TEN));
        System.out.println(r);
        assertTrue(r.approved());
    }

    @Test
    void falla() {
        assertThrows(RestClientException.class,
                () -> client.validate(new ValidacionPagoRequest(UUID.randomUUID(), "FAIL", BigDecimal.TEN)));
    }

    @Test
    void lento() {
        var inicio = System.currentTimeMillis();
        assertThrows(Exception.class,
                () -> client.validate(new ValidacionPagoRequest(UUID.randomUUID(), "SLOW", BigDecimal.TEN)));
        System.out.println("fallo tras " + (System.currentTimeMillis() - inicio) + "ms");
    }

}
