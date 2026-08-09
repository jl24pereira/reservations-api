package com.pereira.api.security.dto;

import java.time.OffsetDateTime;

/**
 *
 * @author Jose Luis Pereira
 */
public record TokenResponse(String token, String tipo, OffsetDateTime expiraEn) {
    public static TokenResponse bearer(String token, OffsetDateTime expiraEn) {
        return new TokenResponse(token, "Bearer", expiraEn);
    }
}
