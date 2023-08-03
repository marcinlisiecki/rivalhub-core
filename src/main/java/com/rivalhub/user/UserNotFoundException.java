package com.rivalhub.user;

import com.rivalhub.common.ErrorMessages;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(ErrorMessages.USER_NOT_FOUND);
    }
}
