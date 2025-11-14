package com.musicfly.backend.models.DAO;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Set;

/**
 * Entidad que representa a un artista en la aplicación UnderSounds.
 *
 * Un artista tiene información básica como su nombre artístico, descripción,
 * estado de verificación, datos bancarios (como el IBAN y propietario de la cuenta),
 * enlaces a redes sociales y estadísticas de streaming mensual. Esta clase hereda
 * de la clase {@link User}, lo que significa que un artista también tiene las propiedades
 * y funcionalidades de un usuario común, pero extendidas con los atributos propios de un artista.
 *
 * Además, un artista puede tener múltiples enlaces a redes sociales asociados a su perfil.
 * El campo `isTrending` permite identificar a los artistas populares en la plataforma.
 * También puede tener múltiples géneros musicales asociados.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Artist extends User {

    @Column(unique = true)
    public String artisticName;  // Nombre artístico del artista

    @Column
    public String description;  // Descripción del artista

    @Column
    public boolean isTrending;  // Indicador de si el artista está de moda

    @Column
    public boolean verified;  // Estado de verificación del artista

    @Column(unique = true)
    public String iban;  // IBAN del artista para pagos

    @Column
    public String accountPropietary;  // Propietario de la cuenta bancaria asociada al IBAN

    public String profilePictureName;

    @OneToMany
    @JoinColumn(name = "social_media_links",
            foreignKey = @ForeignKey(name = "fk_artist_social_media_links",
                    foreignKeyDefinition = "FOREIGN KEY (social_media_links) REFERENCES social_media_links(id)"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public List<SocialMediaLinks> socialMediaLinks;  // Enlaces a redes sociales asociadas al artista

    @Column
    public Long monthlyStreams;  // Número de streams mensuales del artista

    /**
     * Relación muchos a muchos entre artista y géneros musicales.
     * Un artista puede estar asociado a varios géneros musicales.
     */
    @ManyToMany
    @JoinTable(
            name = "artist_genre",  // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"),  // Columna para el artista
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id")  // Columna para el género
    )
    @OnDelete(action = OnDeleteAction.CASCADE)  // Si el artista es eliminado, se eliminan las asociaciones con géneros
    public Set<Genre> genres;  // Géneros musicales asociados al artista

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }


}