package com.musicfly.backend.models.DAO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.musicfly.backend.models.GenreType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Representa un género musical en la plataforma UnderSounds.
 *
 * Cada género está asociado a varios tipos de canciones, y un género puede
 * estar presente en varias canciones dentro del sistema. La clase incluye un
 * campo `type` que hace uso de la enumeración {@link GenreType} para representar
 * el tipo específico de género musical (por ejemplo, Rock, Pop, Jazz, etc.).
 * Además, el género tiene un campo `gender`, que es el nombre textual del género,
 * y una relación muchos a muchos con la entidad {@link Song}, permitiendo asociar
 * varias canciones a un género determinado.
 *
 * La clase incluye los siguientes campos:
 * - `id`: Identificador único generado automáticamente para cada género.
 * - `type`: Tipo del género musical, representado mediante la enumeración {@link GenreType}.
 * - `songs`: Relación muchos a muchos con las canciones que pertenecen a este género musical.
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    public GenreType type;

    /**
     * Constructor que crea un género a partir de un tipo específico de {@link GenreType}.
     * El nombre del género se establece automáticamente en base al valor del tipo.
     *
     * @param genreType Tipo de género musical (por ejemplo, ROCK, POP, JAZZ, etc.)
     */
    public Genre(GenreType genreType) {
        this.type = genreType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id);
    }
}
