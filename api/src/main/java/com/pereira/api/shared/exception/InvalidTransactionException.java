package com.pereira.api.shared.exception;

import java.text.MessageFormat;

import com.pereira.api.reserva.domain.EstadoReserva;

/**
 *
 * @author Jose Luis Pereira
 */
public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(EstadoReserva estado, String accion) {
        super(MessageFormat.format("No se puede {0} una reserva en estado: {1}", accion, estado));
    }

}
