package com.pereira.api.security.model;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.pereira.api.usuario.domain.Usuario;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Jose Luis Pereira
 */
public class AuthenticatedUser implements UserDetails {

    private final Usuario usuario;

    public AuthenticatedUser(Usuario usuario) {
        this.usuario = usuario;
    }

    public UUID getId() {
        return usuario.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(MessageFormat.format("ROLE_{0}", usuario.getRol().name())));
    }

    @Override
    public @Nullable String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo();
    }

}
