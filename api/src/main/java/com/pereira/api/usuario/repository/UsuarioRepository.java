package com.pereira.api.usuario.repository;

import java.util.Optional;
import java.util.UUID;

import com.pereira.api.usuario.domain.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
}
