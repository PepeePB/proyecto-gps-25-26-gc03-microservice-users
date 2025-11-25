package com.musicfly.backend.models.DAO;

import com.musicfly.backend.models.RoleList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Representa un usuario de la plataforma UnderSounds.
 *
 * La clase `User` es la entidad principal que almacena la información básica sobre
 * los usuarios de la plataforma. Cada usuario tiene detalles personales como su
 * nombre, apellido, nombre de usuario, correo electrónico, contraseña, entre otros.
 * Además, incluye un campo que indica si el usuario es un artista, lo que permitirá
 * diferenciarlos de otros tipos de usuarios.
 *
 * Los campos clave de esta entidad incluyen:
 * - `id`: Identificador único del usuario.
 * - `name`: Nombre del usuario.
 * - `surname`: Apellido del usuario.
 * - `username`: Nombre de usuario único.
 * - `email`: Correo electrónico único.
 * - `password`: Contraseña del usuario.
 * - `phone`: Número de teléfono único.
 * - `isArtist`: Indica si el usuario es un artista.
 * - `bio`: Biografía del usuario.
 * - `personalLink`: Enlace personal o sitio web del usuario.
 * - `birthday`: Fecha de nacimiento del usuario.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String surname;

    @Column(unique = true, nullable = false)
    public String username;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String password;

    @Enumerated(EnumType.STRING)
    public RoleList role;

    @Column(unique = true)
    public String phone;

    @Column(nullable = false)
    public boolean isArtist;

    @Column(length = 1000)
    public String bio;

    @Column
    public String personalLink;

    @Temporal(TemporalType.DATE)
    @Column
    public Date birthday;

    @Column
    public boolean verified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
