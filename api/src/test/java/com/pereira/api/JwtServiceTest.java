package com.pereira.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.pereira.api.security.service.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author Jose Luis Pereira
 */
@SpringBootTest
class JwtServiceTest {

    @Autowired
    JwtService jwt;

    @Test
    void generateAndValidate() {
        var token = jwt.generate("admin@coworking.com", "ADMIN");
        System.out.println("TOKEN: " + token);
        assertTrue(jwt.isValid(token));
        assertEquals("admin@coworking.com", jwt.extractEmail(token));

        var partes = token.split("\\.");
        var payloadFalso = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"test@evil.com\",\"rol\":\"ADMIN\"}"
                        .getBytes(StandardCharsets.UTF_8));
        var manipulado = partes[0] + "." + payloadFalso + "." + partes[2];

        assertFalse(jwt.isValid(manipulado));
        assertFalse(jwt.isValid("no-es-un-jwt"));
    }
}
