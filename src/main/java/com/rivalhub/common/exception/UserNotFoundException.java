package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(ErrorMessages.USER_NOT_FOUND);
    }
}
