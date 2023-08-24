package com.rivalhub.auth;

import com.rivalhub.common.dto.ErrorMessageDto;
import com.rivalhub.common.exception.UserNotAuthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthErrorHandler {

    @ExceptionHandler({BadCredentialsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadCredentials(Exception e) {
        return new ErrorMessageDto(e.getMessage());
    }

    @ExceptionHandler({UserNotAuthenticatedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessageDto handleNotAuthenticated(Exception e) {
        return new ErrorMessageDto(e.getMessage());
    }
}
