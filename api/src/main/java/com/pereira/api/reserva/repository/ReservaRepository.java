package com.pereira.api.reserva.repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import com.pereira.api.reserva.domain.EstadoReserva;
import com.pereira.api.reserva.domain.Reserva;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    @Query("""
            select count(r) > 0 from Reserva r
            where r.espacio.id = :espacioId
              and r.estado <> com.pereira.api.reserva.domain.EstadoReserva.CANCELLED
              and r.inicio < :fin
              and r.fin > :inicio
            """)
    boolean overExists(@Param("espacioId") UUID espacioId,
            @Param("inicio") OffsetDateTime inicio,
            @Param("fin") OffsetDateTime fin);

    @Query(value = """
            select r from Reserva r
            join fetch r.espacio
            join fetch r.usuario
            where (:usuarioId is null or r.usuario.id = :usuarioId)
              and (:espacioId is null or r.espacio.id = :espacioId)
              and (:estado is null or r.estado = :estado)
              and (:desde is null or r.inicio >= :desde)
              and (:hasta is null or r.fin <= :hasta)
            """, countQuery = """
            select count(r) from Reserva r
            where (:usuarioId is null or r.usuario.id = :usuarioId)
              and (:espacioId is null or r.espacio.id = :espacioId)
              and (:estado is null or r.estado = :estado)
              and (:desde is null or r.inicio >= :desde)
              and (:hasta is null or r.fin <= :hasta)
            """)
    Page<Reserva> find(@Param("usuarioId") UUID usuarioId,
            @Param("espacioId") UUID espacioId,
            @Param("estado") EstadoReserva estado,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta,
            Pageable pageable);

    @Query("select r from Reserva r join fetch r.espacio join fetch r.usuario where r.id = :id")
    Optional<Reserva> findByIdConDetalle(@Param("id") UUID id);

}
