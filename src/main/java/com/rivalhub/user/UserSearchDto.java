package com.rivalhub.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDto {
    private long id;
    private String name;
    private String email;
    private String profilePictureUrl;

    public UserSearchDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}