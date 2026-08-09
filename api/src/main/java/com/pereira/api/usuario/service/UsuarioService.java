package com.pereira.api.usuario.service;

import com.pereira.api.security.dto.RegistroRequest;
import com.pereira.api.shared.exception.RegisteredEmailException;
import com.pereira.api.usuario.domain.Rol;
import com.pereira.api.usuario.domain.Usuario;
import com.pereira.api.usuario.dto.UsuarioResponse;
import com.pereira.api.usuario.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (repository.existsByEmail(request.email()))
            throw new RegisteredEmailException(request.email());

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre().trim());
        usuario.setRol(Rol.USER);
        usuario.setActivo(true);

        return UsuarioResponse.from(repository.save(usuario));
    }

}
