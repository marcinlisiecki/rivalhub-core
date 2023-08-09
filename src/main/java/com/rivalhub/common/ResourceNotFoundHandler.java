package com.rivalhub.common;

import com.rivalhub.common.dto.ErrorMessageDto;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.exception.ReservationIsNotPossible;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.station.StationNotFoundException;
import com.rivalhub.user.UserAlreadyExistsException;
import com.rivalhub.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResourceNotFoundHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            StationNotFoundException.class,
            OrganizationNotFoundException.class,
            WrongInvitationException.class,

            ReservationIsNotPossible.class,
            AlreadyInOrganizationException.class,
            UserAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDto handleResourceNotFound(Exception e) {
        return new ErrorMessageDto(e.getMessage());
    }
}