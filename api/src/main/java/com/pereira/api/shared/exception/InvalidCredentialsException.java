package com.pereira.api.shared.exception;

/**
 *
 * @author Jose Luis Pereira
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales invalidas");
    }

}
