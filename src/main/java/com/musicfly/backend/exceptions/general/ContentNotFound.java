package com.musicfly.backend.exceptions.general;

/**
 * Excepción personalizada que se lanza cuando no se encuentra el contenido solicitado.
 * Esta clase extiende RuntimeException.
 */
public class ContentNotFound extends RuntimeException {

    /**
     * Constructor que crea una excepción con un mensaje personalizado.
     * Este mensaje describe la razón por la que no se pudo encontrar el contenido.
     *
     * @param message El mensaje que describe el motivo de la excepción.
     */
    public ContentNotFound(String message) {
        super(message);
    }
}
