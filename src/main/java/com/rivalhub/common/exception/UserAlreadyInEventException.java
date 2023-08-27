package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class UserAlreadyInEventException extends RuntimeException {

    public UserAlreadyInEventException() {
        super(ErrorMessages.USER_ALREADY_IN_EVENT);
    }

}
