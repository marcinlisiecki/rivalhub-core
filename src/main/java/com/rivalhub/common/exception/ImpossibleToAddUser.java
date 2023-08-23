package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class ImpossibleToAddUser extends RuntimeException{
    public ImpossibleToAddUser(){
        super(ErrorMessages.IMPOSSIBLE_TO_ADD_USER);
    }
}
