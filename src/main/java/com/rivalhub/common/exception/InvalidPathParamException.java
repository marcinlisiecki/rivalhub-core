package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class InvalidPathParamException extends RuntimeException{
    public InvalidPathParamException(){
        super(ErrorMessages.INVALID_PATH_PARAM_EXCEPTION);
    }
}
