package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class EventNotFoundException  extends RuntimeException{
    public EventNotFoundException() {
        super(ErrorMessages.EVENT_NOT_FOUND);
    }

}
