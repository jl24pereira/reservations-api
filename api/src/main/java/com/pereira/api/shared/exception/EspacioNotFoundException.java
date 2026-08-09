package com.pereira.api.shared.exception;

import java.text.MessageFormat;
import java.util.UUID;

/**
 *
 * @author Jose Luis Pereira
 */
public class EspacioNotFoundException extends RuntimeException {

    public EspacioNotFoundException(UUID id) {
        super(MessageFormat.format("No existe espacio con ID: {0}", id));
    }

}
