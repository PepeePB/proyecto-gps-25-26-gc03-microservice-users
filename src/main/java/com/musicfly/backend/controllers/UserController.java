package com.musicfly.backend.controllers;

import com.musicfly.backend.services.UserService;
import com.musicfly.backend.views.DTO.UserProfileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador REST para la gestión de usuarios.
 * Permite consultar, actualizar y eliminar usuarios del sistema.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     *
     * @param id el ID del usuario.
     * @return los datos del usuario si existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        Optional<UserProfileDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza el perfil de un usuario.
     *
     * @param id el ID del usuario.
     * @param updatedUser los nuevos datos del usuario.
     * @return el perfil actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserProfileDTO updatedUser) {

        try {
            UserProfileDTO updated = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id el ID del usuario.
     * @return código 204 si se eliminó correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
