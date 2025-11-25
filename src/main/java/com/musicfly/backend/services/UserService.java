package com.musicfly.backend.services;

import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.views.DTO.UserProfileDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con el usuario.
 * Proporciona métodos para obtener, actualizar y eliminar usuarios en el sistema.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor de la clase {@link UserService}.
     *
     * @param userRepository el repositorio de usuarios utilizado para interactuar con la base de datos.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Convierte un objeto {@link User} a un objeto {@link UserProfileDTO}.
     *
     * @param user el objeto {@link User} que se convertirá a {@link UserProfileDTO}.
     * @return un objeto {@link UserProfileDTO} que contiene los datos del usuario.
     */
    private UserProfileDTO convertToUserProfileDTO(User user) {
        return new UserProfileDTO(
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPersonalLink(),
                user.getBirthday(),
                user.getBio()
        );
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id el ID del usuario que se desea obtener.
     * @return un {@link Optional} que contiene un {@link UserProfileDTO} si el usuario existe, o vacío si no se encuentra.
     */
    public Optional<UserProfileDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToUserProfileDTO);
    }

    /**
     * Actualiza los datos del perfil de un usuario.
     *
     * @param id el ID del usuario que se desea actualizar.
     * @param updatedUser el nuevo objeto {@link UserProfileDTO} con los datos a actualizar.
     * @return un objeto {@link UserProfileDTO} con los detalles del usuario actualizado.
     * @throws RuntimeException si el usuario no se encuentra en la base de datos.
     */
    public UserProfileDTO updateUser(Long id, UserProfileDTO updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPersonalLink(updatedUser.getPersonalLink());
        user.setBirthday(updatedUser.getBirthday());
        user.setBio(updatedUser.getBio());

        User updatedUserEntity = userRepository.save(user);
        return convertToUserProfileDTO(updatedUserEntity);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id el ID del usuario que se desea eliminar.
     * @throws RuntimeException si el usuario no se encuentra.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado.");
        }
        userRepository.deleteById(id);
    }
}
