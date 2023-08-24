package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException() {
        super(ErrorMessages.USER_ALREADY_EXISTS);
    }

}
