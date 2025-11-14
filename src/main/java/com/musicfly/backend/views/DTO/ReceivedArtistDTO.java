package com.musicfly.backend.views.DTO;

import com.musicfly.backend.models.DAO.SocialMediaLinks;
import com.musicfly.backend.models.GenreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ReceivedArtistDTO {
    private String username;
    private String artisticName;
    private String description;
    private boolean verified;
    private String iban;
    private String accountPropietary;
    private boolean trending;
    private Set<GenreType> genres;
    private List<SocialMediaLinks> socialMediaLinks;
}
