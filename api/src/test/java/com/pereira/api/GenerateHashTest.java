package com.pereira.api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author Jose Luis Pereira
 */
public class GenerateHashTest {

    @Test
    void generate() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("HASH: " + hash);
        System.out.println("VERIFY: " + encoder.matches("admin123", hash));
    }

}
