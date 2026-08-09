package com.pereira.api.security.controller;

import com.pereira.api.security.dto.LoginRequest;
import com.pereira.api.security.dto.RegistroRequest;
import com.pereira.api.security.dto.TokenResponse;
import com.pereira.api.security.service.JwtService;
import com.pereira.api.shared.exception.InvalidCredentialsException;
import com.pereira.api.usuario.dto.UsuarioResponse;
import com.pereira.api.usuario.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return usuarioService.registrar(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email().toLowerCase().trim(), request.password()));

            String authorities = auth.getAuthorities().iterator().next().getAuthority();

            String rol = authorities.replace("ROLE_", "");

            String token = jwtService.generate(auth.getName(), rol);

            return TokenResponse.bearer(token, jwtService.expirationFrom(token));

        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }
}
