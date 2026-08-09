package com.pereira.api.reserva.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.pereira.api.reserva.domain.EstadoReserva;
import com.pereira.api.reserva.domain.Reserva;

/**
 *
 * @author Jose Luis Pereira
 */
public record ReservaResponse(
        UUID id,
        UUID espacioId,
        String espacioNombre,
        UUID usuarioId,
        OffsetDateTime inicio,
        OffsetDateTime fin,
        EstadoReserva estado,
        BigDecimal montoTotal,
        OffsetDateTime creadoEn) {

    public static ReservaResponse from(Reserva r) {
        return new ReservaResponse(
                r.getId(),
                r.getEspacio().getId(),
                r.getEspacio().getNombre(),
                r.getUsuario().getId(),
                r.getInicio(),
                r.getFin(),
                r.getEstado(),
                r.getMontoTotal(),
                r.getCreadoEn());
    }

}
