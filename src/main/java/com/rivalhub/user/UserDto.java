package com.rivalhub.user;

import com.rivalhub.common.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor

public class UserDto {

    private Long id;
    @Size(min = 3, max = 256,message = ErrorMessages.NAME_DONT_FIT_SIZE)
    private String name;
    @Email(message = ErrorMessages.EMAIL_IS_NOT_VALID)
    private String email;
    //@Length(min=8,message = ErrorMessages.PASSWORD_IS_TOO_SHORT)
    private String password;

    private String activationHash;

    public UserDto(Long id, String name,String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
