package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class MatchNotFoundException extends RuntimeException{
    public MatchNotFoundException() {
        super(ErrorMessages.MATCH_NOT_FOUND);
    }

}
