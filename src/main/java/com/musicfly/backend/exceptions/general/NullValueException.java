package com.musicfly.backend.exceptions.general;

/**
 * Excepción personalizada que se lanza cuando un valor requerido es nulo.
 * Esta clase extiende RuntimeException.
 */
public class NullValueException extends RuntimeException {

    /**
     * Constructor que crea una excepción con un mensaje personalizado.
     * Este mensaje describe la razón por la que el valor es nulo y no debería serlo.
     *
     * @param message El mensaje que describe el motivo de la excepción.
     */
    public NullValueException(String message) {
        super(message);
    }
}
