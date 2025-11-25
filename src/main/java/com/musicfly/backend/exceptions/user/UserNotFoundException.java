package com.musicfly.backend.exceptions.user;

/**
 * Excepción personalizada que se lanza cuando un usuario no se encuentra en el sistema
 * según su ID.
 * Esta clase extiende la excepción RuntimeException y proporciona un mensaje detallado
 * que indica el motivo del error.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor que crea una excepción con un mensaje personalizado cuando un usuario
     * no es encontrado en el sistema.
     *
     * @param id El ID del usuario que no fue encontrado.
     */
    public UserNotFoundException(Long id) {
        super("User with ID " + id + " not found.");
    }
}
