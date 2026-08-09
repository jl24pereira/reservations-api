package com.pereira.api.shared.exception;

import java.text.MessageFormat;

/**
 *
 * @author Jose Luis Pereira
 */
public class RegisteredEmailException extends RuntimeException {

    public RegisteredEmailException(String message) {
        super(MessageFormat.format("El email ya esta registrado: {0}", message));
    }

}
