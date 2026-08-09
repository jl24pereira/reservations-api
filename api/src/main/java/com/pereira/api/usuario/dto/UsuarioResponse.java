package com.pereira.api.usuario.dto;

import java.util.UUID;

import com.pereira.api.usuario.domain.Usuario;

/**
 *
 * @author Jose Luis Pereira
 */
public record UsuarioResponse(UUID id, String email, String nombre, String rol) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getNombre(), u.getRol().name());
    }
}
