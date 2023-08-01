package com.rivalhub.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
public class ErrorMessageDto {
    String errorMessage;
    public ErrorMessageDto(String errorMessage){
        this.errorMessage = errorMessage;
    }
}
