package com.rivalhub.user;

import com.rivalhub.common.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

@Getter
@Setter
@NoArgsConstructor
public class UserDisplayDTO {
    private long id;
    private String name;
    private String email;
    private String profilePictureUrl;
}
