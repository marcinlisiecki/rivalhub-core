package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class AlreadyEventParticipantException extends RuntimeException{
    public AlreadyEventParticipantException() {
        super(ErrorMessages.YOU_ARE_ALREADY_PARTICIPANT);
    }
}