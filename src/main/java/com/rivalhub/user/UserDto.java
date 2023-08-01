package com.rivalhub.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;

    public UserDto(Long id, String name,String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
