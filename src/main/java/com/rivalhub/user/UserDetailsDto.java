package com.rivalhub.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class UserDetailsDto {
    private long id;
    private String name;
    private String email;
    private String profilePictureUrl;
}
