package com.musicfly.backend.exceptions.artist;

/**
 * Excepción personalizada que se lanza cuando no se encuentra un artista en el sistema.
 * Esta clase extiende RuntimeException y proporciona dos constructores para crear un mensaje
 * de error personalizado basado en el ID o el correo electrónico del artista.
 */
public class ArtistNotFoundException extends RuntimeException {

    /**
     * Constructor que crea una excepción con un mensaje indicando que el artista con el ID
     * proporcionado no fue encontrado.
     *
     * @param id El ID del artista que no se encontró.
     */
    public ArtistNotFoundException(Long id) {
        super("Artist with ID " + id + " not found.");
    }

    /**
     * Constructor que crea una excepción con un mensaje indicando que el artista con el correo
     * electrónico proporcionado no fue encontrado.
     *
     * @param email El correo electrónico del artista que no se encontró.
     */
    public ArtistNotFoundException(String email) {
        super("Artist with email " + email + " not found.");
    }
}
