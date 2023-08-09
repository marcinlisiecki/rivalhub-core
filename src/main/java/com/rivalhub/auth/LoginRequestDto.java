package com.rivalhub.auth;

import com.rivalhub.common.ErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotNull(message = ErrorMessages.EMAIL_IS_REQUIRED)
    private String email;

    @NotNull(message = ErrorMessages.PASSWORD_IS_REQUIRED)
    private String password;
}
