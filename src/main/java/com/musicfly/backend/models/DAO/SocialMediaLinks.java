package com.musicfly.backend.models.DAO;

import com.musicfly.backend.models.SocialMediaTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa los enlaces de redes sociales asociados a un artista en la plataforma UnderSounds.
 *
 * Esta clase permite almacenar los enlaces de redes sociales de los artistas, donde cada enlace
 * está asociado a un tipo específico de red social (Instagram, Spotify, YouTube, etc.) a través
 * de la enumeración {@link SocialMediaTypes}. Los artistas pueden tener varios enlaces asociados
 * a diferentes plataformas de redes sociales, y esta relación se establece mediante un vínculo
 * con la entidad {@link Artist}.
 *
 * Los campos de la clase incluyen:
 * - `id`: Identificador único de cada enlace, generado automáticamente.
 * - `link`: URL del enlace a la red social.
 * - `type`: Tipo de red social, representado por la enumeración {@link SocialMediaTypes}.
 * - `artist`: Relación con la entidad {@link Artist}, indicando a qué artista pertenece el enlace.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SocialMediaLinks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column
    public String link;  // URL del enlace a la red social

    @Enumerated(EnumType.STRING)
    public SocialMediaTypes type;  // Tipo de red social (Instagram, Spotify, YouTube, etc.)

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false)
    public Artist artist;  // Relación con el artista propietario del enlace

}
