package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class InsufficientPermissionsException extends RuntimeException{
    public InsufficientPermissionsException() {
        super(ErrorMessages.INSUFFICIENT_PERMISSIONS);
    }
}
