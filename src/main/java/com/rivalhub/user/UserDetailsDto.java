package com.rivalhub.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsDto {

    private long id;
    private String name;
    private String email;
    private String profilePictureUrl;
}
