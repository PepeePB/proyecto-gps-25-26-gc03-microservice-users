package com.musicfly.backend.views.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * DTO Class.
 * Sirve para almacenar el token, que no es más que una
 * cadena de texto. Nos servira como filtro de la respuesta
 * en el controller
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    String token;
    String state;

}
