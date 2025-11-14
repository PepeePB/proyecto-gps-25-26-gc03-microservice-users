package com.musicfly.backend.repositories;

import com.musicfly.backend.models.DAO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para gestionar los datos de los usuarios de la plataforma.
 * Contiene métodos personalizados para verificar el login, existencia de email, id,
 * y también para verificar duplicados de nombre de usuario o teléfono.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Consulta para el login del usuario, buscando por su email y contraseña.
     * Se utiliza para verificar si existe un usuario con esos datos para poder iniciar sesión.
     *
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return Un objeto `Optional<User>` que contiene el usuario si existe, o vacío si no existe.
     */
    Optional<User> findByEmailAndPassword(String email, String password);

    /**
     * Consulta para verificar si ya existe un usuario con el mismo email.
     * Se utiliza para verificar si el email ya está registrado en el sistema durante el proceso de registro.
     *
     * @param email El email del usuario que se desea verificar.
     * @return Un objeto `Optional<User>` que contiene el usuario si existe, o vacío si no existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Consulta para verificar si ya existe un usuario con el mismo ID.
     * Se utiliza para verificar la existencia de un usuario cuando se busca por su identificador único.
     *
     * @param id El ID del usuario que se desea verificar.
     * @return Un objeto `Optional<User>` que contiene el usuario si existe, o vacío si no existe.
     */
    Optional<User> findById(Long id);

    /**
     * Consulta para verificar si ya existe un usuario con el mismo nombre de usuario.
     * Se utiliza para verificar la existencia de un nombre de usuario duplicado durante el proceso de registro.
     *
     * @param username El nombre de usuario que se desea verificar.
     * @return Un objeto `Optional<User>` que contiene el usuario si existe, o vacío si no existe.
     */
    Optional<User> findByUsername(String username);

    /**
     * Consulta para verificar si ya existe un usuario con el mismo número de teléfono.
     * Se utiliza para verificar la existencia de un teléfono duplicado durante el proceso de registro.
     *
     * @param phone El número de teléfono que se desea verificar.
     * @return Un objeto `Optional<User>` que contiene el usuario si existe, o vacío si no existe.
     */
    Optional<User> findByPhone(String phone);
}
