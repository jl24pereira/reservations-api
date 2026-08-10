package com.pereira.api.reserva.state;

import java.util.EnumMap;
import java.util.Map;

import com.pereira.api.reserva.domain.EstadoReserva;

/**
 *
 * @author Jose Luis Pereira
 */
public final class ReservaStateFactory {

    private static final Map<EstadoReserva, ReservaState> ESTADOS = new EnumMap<>(Map.of(
            EstadoReserva.PENDING, new PendienteState(),
            EstadoReserva.PENDING_PAYMENT, new PendientePagoState(),
            EstadoReserva.CONFIRMED, new ConfirmadaState(),
            EstadoReserva.CANCELLED, new CanceladaState(),
            EstadoReserva.COMPLETED, new CompletadaState()));

    private ReservaStateFactory() {
    }

    public static ReservaState of(EstadoReserva estado) {
        return ESTADOS.get(estado);
    }

}
