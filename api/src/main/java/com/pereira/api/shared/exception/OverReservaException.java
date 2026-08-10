package com.pereira.api.shared.exception;

/**
 *
 * @author Jose Luis Pereira
 */
public class OverReservaException extends RuntimeException {

    public OverReservaException() {
        super("Ya existe reserva para ese espacio en horario solicitado");
    }

}
