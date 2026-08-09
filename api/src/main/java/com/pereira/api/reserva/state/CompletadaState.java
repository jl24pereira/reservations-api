package com.pereira.api.reserva.state;

import com.pereira.api.reserva.domain.EstadoReserva;

/**
 *
 * @author Jose Luis Pereira
 */
public class CompletadaState implements ReservaState {

    @Override
    public EstadoReserva estado() {
        return EstadoReserva.COMPLETED;
    }

}
