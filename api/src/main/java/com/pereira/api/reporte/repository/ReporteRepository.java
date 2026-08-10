package com.pereira.api.reporte.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.pereira.api.espacio.domain.Espacio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Espacio, UUID> {

    @Query(value = """
            select e.id                as "espacioId",
                   e.nombre            as "espacioNombre",
                   coalesce(sum(extract(epoch from (r.fin - r.inicio))) / 3600, 0) as "horasReservadas",
                   count(r.id)         as "totalReservas"
            from espacio e
            left join reserva r
                   on r.espacio_id = e.id
                  and r.estado in ('CONFIRMED', 'COMPLETED')
                  and r.inicio >= :desde
                  and r.fin    <= :hasta
            where e.activo = true
            group by e.id, e.nombre
            order by e.nombre
            """, nativeQuery = true)
    List<OcupacionProjection> calcularOcupacion(@Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta);
}
