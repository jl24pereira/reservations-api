package com.pereira.api.reserva.repository;

import java.util.UUID;

import com.pereira.api.reserva.domain.Reserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

}
