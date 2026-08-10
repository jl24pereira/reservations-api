package com.pereira.api.reserva.event;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public record ReservaConfirmadaEvent(
        UUID reservaId,
        String emailUsuario,
        String nombreEspacio,
        OffsetDateTime inicio,
        OffsetDateTime fin) {

}
