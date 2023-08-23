package com.rivalhub.common;

import com.rivalhub.common.dto.ErrorMessageDto;
import com.rivalhub.common.exception.*;
import com.rivalhub.common.exception.EmailNotSentException;
import com.rivalhub.common.exception.MatchNotFoundException;
import io.jsonwebtoken.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({
            UserNotFoundException.class,
            StationNotFoundException.class,
            OrganizationNotFoundException.class,
            WrongInvitationException.class,
            ReservationIsNotPossible.class,
            AlreadyInOrganizationException.class,
            UserAlreadyExistsException.class,
            InsufficientPermissionsException.class,
            EmailNotSentException.class,
            UserNotFoundException.class,
            MatchNotFoundException.class,
            IOException.class,
            MatchNotFoundException.class,
            InvalidPathParamException.class,
            ImpossibleToAddUser.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDto handleExceptions(Exception e) {
        return new ErrorMessageDto(e.getMessage());
    }
}
