package com.musicfly.backend.repositories;

import com.musicfly.backend.models.DAO.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad `Artist`, que proporciona operaciones CRUD y consultas adicionales
 * relacionadas con los artistas en la plataforma. Este repositorio extiende `JpaRepository`,
 * lo que permite realizar operaciones estándar de JPA como la creación, lectura, actualización y eliminación
 * de registros, junto con consultas personalizadas para obtener artistas específicos.
 *
 * Los métodos definidos en este repositorio incluyen:
 * - Obtener los artistas que están marcados como "trending" (de tendencia).
 * - Buscar un artista por su `id`.
 * - Buscar un artista por su `email`.
 * - Buscar artistas por su nombre artístico de manera insensible a mayúsculas/minúsculas.
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    /**
     * Encuentra los artistas que están marcados como "trending".
     *
     * @return Una lista de artistas que son considerados tendencia.
     */
    List<Artist> findByIsTrendingTrue();

    /**
     * Encuentra un artista por su identificador único (`id`).
     *
     * @param id El identificador único del artista.
     * @return Un `Optional` que contiene el artista si se encuentra, o vacío si no.
     */
    Optional<Artist> findById(Long id);

    /**
     * Encuentra un artista por su correo electrónico (`email`).
     *
     * @param email El correo electrónico del artista.
     * @return Un `Optional` que contiene el artista si se encuentra, o vacío si no.
     */
    Optional<Artist> findByEmail(String email);

    /**
     * Encuentra artistas cuyo nombre artístico contenga la cadena proporcionada,
     * ignorando mayúsculas y minúsculas.
     *
     * @param name El nombre artístico del artista.
     * @return Una lista de artistas cuyo nombre artístico contiene la cadena dada.
     */
    List<Artist> findByArtisticNameContainingIgnoreCase(String name);

    /**
     * Suma el total de streams mensuales de todos los artistas.
     *
     * @return La suma total de monthlyStreams de todos los artistas.
     */
    @Query("SELECT SUM(a.monthlyStreams) FROM Artist a")
    Long getTotalMonthlyStreams();

    Optional<Artist> findByUsername(String username);
}
