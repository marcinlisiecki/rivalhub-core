package com.rivalhub.organization.exception;

import com.rivalhub.common.ErrorMessages;

public class InsufficientPermissionsException extends RuntimeException{
    public InsufficientPermissionsException() {
        super(ErrorMessages.INSUFFICIENT_PERMISSIONS);
    }
}
