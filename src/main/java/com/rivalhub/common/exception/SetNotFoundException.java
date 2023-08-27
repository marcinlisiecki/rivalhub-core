package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class SetNotFoundException extends RuntimeException {

    public SetNotFoundException() {
        super(ErrorMessages.SET_NOT_FOUND);
    }
}
