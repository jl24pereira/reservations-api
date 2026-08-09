package com.pereira.api.reserva.state;

import com.pereira.api.reserva.domain.EstadoReserva;

/**
 *
 * @author Jose Luis Pereira
 */
public class PendienteState implements ReservaState {

    @Override
    public EstadoReserva estado() {
        return EstadoReserva.PENDING;
    }

    @Override
    public EstadoReserva cancelar() {
        return EstadoReserva.CANCELLED;
    }

    @Override
    public EstadoReserva confirmar() {
        return EstadoReserva.CONFIRMED;
    }

    @Override
    public EstadoReserva pagoPendiente() {
        return EstadoReserva.PENDING_PAYMENT;
    }

}
