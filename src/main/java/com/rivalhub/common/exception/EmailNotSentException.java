package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class EmailNotSentException extends RuntimeException{
    public EmailNotSentException() {
            super(ErrorMessages.EMAIL_NOT_SENT);
    }
}
