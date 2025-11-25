package com.musicfly.backend.exceptions.general;

/**
 * Excepción personalizada que se lanza cuando el contenido proporcionado no es válido
 * o no cumple con los requisitos esperados. Esta clase extiende RuntimeException.
 */
public class BadContentException extends RuntimeException {

    /**
     * Constructor que crea una excepción con un mensaje personalizado.
     * Este mensaje describe la razón por la que el contenido es inválido o incorrecto.
     *
     * @param message El mensaje que describe el motivo de la excepción.
     */
    public BadContentException(String message) {
        super(message);
    }
}
