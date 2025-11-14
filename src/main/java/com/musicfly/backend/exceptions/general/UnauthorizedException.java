package com.musicfly.backend.exceptions.general;

/**
 * Excepción personalizada que se lanza cuando un usuario no tiene permisos para realizar una acción.
 * Esta clase extiende RuntimeException.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructor que crea una excepción con un mensaje personalizado.
     * Este constructor es útil para describir el motivo específico por el cual la operación no está permitida.
     *
     * @param message El mensaje que describe el motivo de la excepción.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
