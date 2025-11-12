package com.musicfly.backend.views.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserGoogleDTO {
    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String email;
    private Boolean email_verified;
    private String locale;
}

