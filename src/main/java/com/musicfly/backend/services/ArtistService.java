package com.musicfly.backend.services;

import com.musicfly.backend.exceptions.artist.ArtistNotFoundException;
import com.musicfly.backend.models.DAO.Artist;
import com.musicfly.backend.models.DAO.Genre;
import com.musicfly.backend.models.DAO.SocialMediaLinks;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.repositories.ArtistRepository;
import com.musicfly.backend.repositories.GenreRepository;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.views.DTO.ArtistFormDTO;
import com.musicfly.backend.views.DTO.ReceivedArtistDTO;
import com.musicfly.backend.views.DTO.SentArtistDTO;
import com.musicfly.backend.views.DTO.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las operaciones relacionadas con los artistas.
 * Incluye la creación, actualización, obtención y eliminación de artistas, así como la conversión entre objetos de tipo {@link Artist} y {@link SentArtistDTO}.
 */
@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;  // Repositorio para géneros

    /**
     * Crea un nuevo artista utilizando la información proporcionada por el DTO de entrada y asociando al usuario indicado.
     *
     * @param dto El DTO recibido con la información del artista a crear.
     * @param userId El ID del usuario que se convertirá en artista.
     *
     * @throws RuntimeException Si no se encuentra el usuario con el ID proporcionado.
     */
    @Transactional
    public void createArtist(ReceivedArtistDTO dto, Long userId) {
        // Busca el usuario por ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Elimina el usuario para luego crear un nuevo artista
        userRepository.delete(user);
        userRepository.flush();

        // Crea un nuevo objeto Artist a partir de los datos recibidos
        Artist artist = Artist.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .phone(user.getPhone())
                .bio(user.getBio())
                .personalLink(user.getPersonalLink())
                .birthday(user.getBirthday())
                .artisticName(dto.getArtisticName())
                .description(dto.getDescription())
                .verified(dto.isVerified())
                .iban(dto.getIban())
                .accountPropietary(dto.getAccountPropietary())
                .isTrending(dto.isTrending())
                .monthlyStreams(0L)
                .socialMediaLinks(new ArrayList<>())
                .isArtist(true)
                .build();

        // Establece la relación de redes sociales si existen
        if (dto.getSocialMediaLinks() != null) {
            for (SocialMediaLinks link : dto.getSocialMediaLinks()) {
                link.setArtist(artist);
                artist.getSocialMediaLinks().add(link);
            }
        }

        // Establece la relación de géneros si existen
        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            Set<Genre> genres = genreRepository.findByTypeIn(dto.getGenres());
            artist.setGenres(genres);
        }

        // Guarda el artista en la base de datos
        artistRepository.save(artist);
    }

    /**
     * Elimina un artista por su nombre de usuario.
     *
     * @param username El nombre de usuario del artista a eliminar.
     * @return El objeto {@link User} creado a partir de los datos del artista eliminado.
     *
     * @throws ArtistNotFoundException Si no se encuentra el artista con el nombre de usuario proporcionado.
     */
    @Transactional
    public User deleteArtistByUsername(String username) {
        // Busca al artista por su nombre de usuario
        Artist artist = artistRepository.findByUsername(username)
                .orElseThrow(() -> new ArtistNotFoundException("No se encontró un artista con el username " + username));

        // Verifica que el artista sea una entidad válida
        if (!artist.isArtist()) {
            throw new IllegalStateException("Esta entidad no es un artista válido");
        }

        // Elimina el artista
        artistRepository.delete(artist);
        artistRepository.flush();

        // Crea un nuevo usuario a partir de los datos del artista eliminado
        User user = User.builder()
                .name(artist.getName())
                .surname(artist.getSurname())
                .username(artist.getUsername())
                .email(artist.getEmail())
                .password(artist.getPassword())
                .phone(artist.getPhone())
                .bio(artist.getBio())
                .personalLink(artist.getPersonalLink())
                .birthday(artist.getBirthday())
                .isArtist(false) // El usuario ya no es un artista
                .build();

        // Guarda el nuevo usuario en la base de datos
        return userRepository.save(user);
    }


    /**
     * Obtiene un artista por su correo electrónico.
     *
     * @param email El correo electrónico del artista.
     * @return Un objeto {@link SentArtistDTO} con la información del artista.
     *
     * @throws ArtistNotFoundException Si no se encuentra el artista con el correo proporcionado.
     */
    public SentArtistDTO getArtistByEmail(String email) {
        Artist artist = artistRepository.findByEmail(email)
                .orElseThrow(() -> new ArtistNotFoundException("No se encontró un artista con el email " + email));
        return mapToSentDTO(artist);
    }

    /**
     * Obtiene un artista por su username.
     *
     * @param username El correo electrónico del artista.
     * @return Un objeto {@link SentArtistDTO} con la información del artista.
     *
     * @throws ArtistNotFoundException Si no se encuentra el artista con el correo proporcionado.
     */
    public SentArtistDTO getArtistByUsername(String username) {
        Artist artist = artistRepository.findByUsername(username)
                .orElseThrow(() -> new ArtistNotFoundException("No se encontró un artista con el username " + username));
        return mapToSentDTO(artist);
    }

    /**
     * Obtiene todos los artistas registrados en la base de datos.
     *
     * @return Una lista de objetos {@link SentArtistDTO} representando a todos los artistas.
     */
    public List<SentArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(this::mapToSentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los 4 artistas más populares que están marcados como en tendencia.
     *
     * @return Una lista de objetos {@link SentArtistDTO} representando a los artistas en tendencia.
     */
    public List<SentArtistDTO> getTopTrendingArtists() {
        return artistRepository.findByIsTrendingTrue().stream()
                .limit(4)
                .map(this::mapToSentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la información de un artista por su ID.
     *
     * @param id El ID del artista.
     * @return Un objeto {@link SentArtistDTO} con los datos del artista.
     *
     * @throws ArtistNotFoundException Si no se encuentra el artista con el ID proporcionado.
     */
    public SentArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        return mapToSentDTO(artist);
    }

    /**
     * Obtiene los artistas que coinciden parcialmente con el nombre artístico proporcionado.
     *
     * @param name El nombre artístico o parte del mismo.
     * @return Una lista de objetos {@link SentArtistDTO} con los artistas que coinciden.
     */
    public List<SentArtistDTO> getArtistsByName(String name) {
        return artistRepository.findByArtisticNameContainingIgnoreCase(name).stream()
                .map(this::mapToSentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapea un objeto {@link Artist} a un {@link SentArtistDTO}.
     *
     * @param artist El artista a mapear.
     * @return Un objeto {@link SentArtistDTO} con la información del artista.
     */
    private SentArtistDTO mapToSentDTO(Artist artist) {
        // Usando el nuevo constructor de SentArtistDTO que acepta un objeto Artist
        SentArtistDTO dto = new SentArtistDTO(artist);

        return dto;
    }

    /**
     * Actualiza la información de un artista existente.
     *
     * @param id El ID del artista a actualizar.
     * @param artistDTO El DTO con los nuevos datos del artista.
     * @return El objeto {@link Artist} actualizado.
     *
     * @throws ArtistNotFoundException Si no se encuentra el artista con el ID proporcionado.
     */
    @Transactional
    public Artist updateArtist(Long id, ReceivedArtistDTO artistDTO) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        // Actualiza campos simples
        artist.setArtisticName(artistDTO.getArtisticName());
        artist.setDescription(artistDTO.getDescription());
        artist.setTrending(artistDTO.isTrending());
        artist.setVerified(artistDTO.isVerified());
        artist.setIban(artistDTO.getIban());
        artist.setAccountPropietary(artistDTO.getAccountPropietary());

        // Actualiza redes sociales
        if (artistDTO.getSocialMediaLinks() != null) {
            // Limpiar la lista existente de enlaces
            artist.getSocialMediaLinks().clear();

            // Guardar el artista con la lista de enlaces vacía
            Artist savedArtist = artistRepository.saveAndFlush(artist);

            // Guardar el artista con los nuevos enlaces
            return artistRepository.save(savedArtist);
        }

        // Actualiza géneros
        if (artistDTO.getGenres() != null && !artistDTO.getGenres().isEmpty()) {
            Set<Genre> genres = genreRepository.findByTypeIn(artistDTO.getGenres());
            artist.setGenres(genres);
        }

        return artistRepository.save(artist);
    }

    /**
     * Revertir un artista a un usuario normal. El artista se elimina y se crea un nuevo usuario con la información del artista.
     *
     * @param id El ID del artista a revertir.
     * @return El nuevo objeto {@link User} creado.
     *
     * @throws ArtistNotFoundException Si no se encuentra el artista con el ID proporcionado.
     * @throws IllegalStateException Si el artista no es una entidad válida.
     */
    @Transactional
    public User revertArtistToUser(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        if (!artist.isArtist()) {
            throw new IllegalStateException("Esta entidad no es un artista válido");
        }

        artistRepository.delete(artist);
        artistRepository.flush();

        // Crea un nuevo usuario a partir de los datos del artista
        User user = User.builder()
                .name(artist.getName())
                .surname(artist.getSurname())
                .username(artist.getUsername())
                .email(artist.getEmail())
                .password(artist.getPassword())
                .phone(artist.getPhone())
                .bio(artist.getBio())
                .personalLink(artist.getPersonalLink())
                .birthday(artist.getBirthday())
                .isArtist(false)
                .build();

        // Guarda el nuevo usuario en la base de datos
        return userRepository.save(user);
    }

    /**
     * Actualiza solo la descripción del artista.
     *
     * @param id El ID del artista.
     * @param nuevaDescripcion La nueva descripción a establecer.
     * @return El objeto {@link Artist} actualizado.
     */
    @Transactional
    public Artist updateDescription(Long id, String nuevaDescripcion) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        artist.setDescription(nuevaDescripcion);
        return artistRepository.save(artist);
    }

    /**
     * Actualiza solo el nombre artístico del artista.
     *
     * @param id El ID del artista.
     * @param nuevoNombreArtistico El nuevo nombre artístico a establecer.
     * @return El objeto {@link Artist} actualizado.
     */
    @Transactional
    public Artist updateArtisticName(Long id, String nuevoNombreArtistico) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        artist.setArtisticName(nuevoNombreArtistico);
        return artistRepository.save(artist);
    }

    public void createArtistFromForm(ArtistFormDTO dto, Long userId) {
        try {
            // Busca el usuario por ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Elimina el usuario para luego crear un nuevo artista
            userRepository.delete(user);
            userRepository.flush();

            // Crea un nuevo objeto Artist a partir de los datos recibidos
            Artist artist = Artist.builder()
                    .name(user.getName())
                    .surname(user.getSurname())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .phone(user.getPhone())
                    .isArtist(true)
                    .bio(user.getBio())
                    .personalLink(user.getPersonalLink())
                    .birthday(user.getBirthday())
                    .artisticName(dto.getArtistName())
                    .description(dto.getDescription())
                    .verified(true)  // Valor explícito
                    .iban(dto.getIban())
                    .accountPropietary(dto.getAccountPropietary())
                    .isTrending(false)  // Valor explícito
                    .monthlyStreams(0L)
                    .socialMediaLinks(new ArrayList<>())
                    .build();

            // Guarda el artista en la base de datos
            artistRepository.save(artist);

            System.out.println("Artista creado con éxito: " + artist.getArtisticName());

        } catch (Exception e) {
            System.err.println("Error creando el artista: " + e.getMessage());
            e.printStackTrace();
        }
    }
}