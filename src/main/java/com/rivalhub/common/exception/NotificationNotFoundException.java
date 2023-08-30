package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class NotificationNotFoundException extends RuntimeException{
    public NotificationNotFoundException(){
        super(ErrorMessages.NOTIFICATION_NOT_FOUND_EXCEPTION);
    }
}
