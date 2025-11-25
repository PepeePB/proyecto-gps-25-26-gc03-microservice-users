package com.musicfly.backend.views.DTO;

import lombok.*;
import java.util.Date;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String personalLink;
    private Date birthday;
    private String bio;

    public UserProfileDTO(String username, String name, String email, String personalLink, Date birthday, String bio) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.personalLink = personalLink;
        this.birthday = birthday;
        this.bio = bio;
    }
}
