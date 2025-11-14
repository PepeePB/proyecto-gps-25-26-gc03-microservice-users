package com.musicfly.backend.views.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * DTO Class.
 * Nos permite establcer que datos son los necesarios para el
 * login del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    String username;
    String password;
}
