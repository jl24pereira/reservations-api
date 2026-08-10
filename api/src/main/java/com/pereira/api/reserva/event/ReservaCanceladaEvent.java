package com.pereira.api.reserva.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public record ReservaCanceladaEvent(
        UUID reservaId,
        String emailUsuario,
        String nombreEspacio,
        OffsetDateTime inicio,
        OffsetDateTime fin) {

}
