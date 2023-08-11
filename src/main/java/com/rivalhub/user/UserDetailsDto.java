package com.rivalhub.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDetailsDto {

    private long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private LocalDateTime activationTime;

    public UserDetailsDto(Long id, String name, String email, String profilePictureUrl, LocalDateTime activationTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.activationTime = activationTime;
    }
}
