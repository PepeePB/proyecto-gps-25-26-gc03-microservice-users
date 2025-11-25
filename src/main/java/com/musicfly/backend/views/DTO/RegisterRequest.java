package com.musicfly.backend.views.DTO;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/***
 * DTO Class.
 * Nos permite establcer que datos son los necesarios para el
 * registro del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    String username;
    String password;
    String name;
    String surname;
    String email;
    String phone;
    Date birthday;
    String gender;
}
