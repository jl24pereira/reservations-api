package com.pereira.api.security.dto;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Jose Luis Pereira
 */
public record LoginRequest(@NotBlank String email, @NotBlank String password) {

}
