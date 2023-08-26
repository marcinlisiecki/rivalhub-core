package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class EventIsNotPublicException extends RuntimeException{
    public EventIsNotPublicException() {
        super(ErrorMessages.EVENT_IS_NOT_PUBLIC);
    }

}