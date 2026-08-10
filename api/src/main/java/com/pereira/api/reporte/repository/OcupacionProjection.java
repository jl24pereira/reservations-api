package com.pereira.api.reporte.repository;

import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public interface OcupacionProjection {
    UUID getEspacioId();

    String getEspacioNombre();

    Double getHorasReservadas();

    Long getTotalReservas();
}
