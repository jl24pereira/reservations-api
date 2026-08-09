package com.pereira.api.security.service;

import com.pereira.api.security.model.AuthenticatedUser;
import com.pereira.api.usuario.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UsuarioRepository repository;

    public CustomUserDetailService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .map(AuthenticatedUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales invalidas"));
    }

}
