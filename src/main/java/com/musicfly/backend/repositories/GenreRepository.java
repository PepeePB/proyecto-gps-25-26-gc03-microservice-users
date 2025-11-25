package com.musicfly.backend.repositories;

import com.musicfly.backend.models.DAO.Genre;
import com.musicfly.backend.models.GenreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repositorio JPA para gestionar los géneros musicales.
 * Extiende de JpaRepository, lo que proporciona acceso a métodos estándar CRUD para la entidad Genre.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Busca un género musical por su tipo.
     * @param type El tipo de género musical a buscar (ej. ROCK, POP, JAZZ).
     * @return Un objeto Optional que puede contener un Genre si se encuentra, o estar vacío si no se encuentra el género.
     */
    Optional<Genre> findByType(GenreType type);

    Set<Genre> findByTypeIn(Set<GenreType> types);
}
