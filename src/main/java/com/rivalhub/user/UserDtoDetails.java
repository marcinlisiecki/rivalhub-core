package com.rivalhub.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDtoDetails {

    private String name;
    private String email;

    public UserDtoDetails(String name,String email){
        this.name = name;
        this.email = email;

    }


}
