package com.musicfly.backend.views.DTO;

import com.musicfly.backend.models.DAO.Artist;
import com.musicfly.backend.models.DAO.Genre;
import com.musicfly.backend.models.DAO.SocialMediaLinks;
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
public class SentArtistDTO {
    private String profilePicture;
    private Long artistId;
    private String username;
    private String artisticName;
    private String description;
    private boolean isTrending;
    private boolean verified;
    private List<SocialMediaLinks> socialMediaLinks;
    private Long monthlyStreams;
    private Set<Genre> genres;

    public SentArtistDTO(Artist artist) {
        this.artisticName = artist.getArtisticName();
        this.username = artist.getUsername();
        this.description = artist.getDescription();
        this.isTrending = artist.isTrending();
        this.verified = artist.isVerified();
        this.socialMediaLinks = artist.getSocialMediaLinks();
        this.monthlyStreams = artist.getMonthlyStreams();
        this.genres = artist.getGenres();
        this.artistId = artist.getId();
        this.profilePicture = createProfilePictureURL(artist);
    }

    private String createProfilePictureURL(Artist artist) {
        return "http://localhost:8080/api/image/profilePictures/artists/" + artist.getId() + '/' + artist.getProfilePictureName();
    }
}
