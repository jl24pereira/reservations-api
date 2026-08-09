package com.pereira.api.espacio.repository;

import java.util.UUID;

import com.pereira.api.espacio.domain.Espacio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, UUID> {

}
