package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class UserAlreadyActivated extends RuntimeException {
    public UserAlreadyActivated() {
        super(ErrorMessages.USER_ALREADY_ACTIVATED);
    }
}
