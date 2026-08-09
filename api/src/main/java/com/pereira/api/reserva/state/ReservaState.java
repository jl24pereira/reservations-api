package com.pereira.api.reserva.state;

import com.pereira.api.reserva.domain.EstadoReserva;
import com.pereira.api.shared.exception.InvalidTransactionException;

/**
 *
 * @author Jose Luis Pereira
 */
public interface ReservaState {

    EstadoReserva estado();

    default EstadoReserva confirmar() {
        throw new InvalidTransactionException(estado(), "confirmar");
    }

    default EstadoReserva pagoPendiente() {
        throw new InvalidTransactionException(estado(), "pendiente de pago");
    }

    default EstadoReserva cancelar() {
        throw new InvalidTransactionException(estado(), "cancelar");
    }

    default EstadoReserva completar() {
        throw new InvalidTransactionException(estado(), "completar");
    }
}
