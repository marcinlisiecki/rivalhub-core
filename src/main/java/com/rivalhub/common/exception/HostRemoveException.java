package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class HostRemoveException extends RuntimeException {
    public HostRemoveException() {
        super(ErrorMessages.HOST_REMOVE_EXCEPTION);
    }

}
