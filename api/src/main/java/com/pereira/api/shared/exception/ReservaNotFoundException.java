package com.pereira.api.shared.exception;

import java.text.MessageFormat;
import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public class ReservaNotFoundException extends RuntimeException {

    public ReservaNotFoundException(UUID id) {
        super(MessageFormat.format("No existe reserva con ID: {0}", id));
    }

}
