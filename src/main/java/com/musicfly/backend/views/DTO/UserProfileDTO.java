package com.musicfly.backend.views.DTO;

import lombok.*;
import java.util.Date;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String personalLink;
    private Date birthday;
    private String bio;

    public UserProfileDTO(String name, String email, String personalLink, Date birthday, String bio) {
        this.name = name;
        this.email = email;
        this.personalLink = personalLink;
        this.birthday = birthday;
        this.bio = bio;
    }
}
