package com.rivalhub.auth;

import com.rivalhub.common.ErrorMessages;

public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException() {
        super(ErrorMessages.NOT_AUTHENTICATED);
    }
}
