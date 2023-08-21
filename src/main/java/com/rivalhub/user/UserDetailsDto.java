package com.rivalhub.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private long id;
    private String name;
    private String email;
    private String profilePictureUrl;
    private LocalDateTime activationTime;

    public UserDetailsDto(long id, String name, String email, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }
}