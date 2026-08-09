package com.pereira.api.pago.repository;

import java.util.UUID;

import com.pereira.api.pago.domain.Pago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, UUID> {

}
