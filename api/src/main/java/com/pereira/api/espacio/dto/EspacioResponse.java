package com.pereira.api.espacio.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.pereira.api.espacio.domain.Espacio;
import com.pereira.api.espacio.domain.TipoEspacio;

/**
 *
 * @author Jose Luis Pereira
 */
public record EspacioResponse(UUID id,
        String nombre,
        TipoEspacio tipo,
        Integer capacidad,
        String ubicacion,
        BigDecimal tarifaHora,
        boolean activo,
        OffsetDateTime creadoen) {

    public static EspacioResponse from(Espacio e) {
        return new EspacioResponse(e.getId(), e.getNombre(), e.getTipo(), e.getCapacidad(), e.getUbicacion(),
                e.getTarifaHora(), e.isActivo(), e.getCreadoEn());
    }
}
