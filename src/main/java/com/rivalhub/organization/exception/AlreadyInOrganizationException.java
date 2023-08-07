package com.rivalhub.organization.exception;

import com.rivalhub.common.ErrorMessages;

public class AlreadyInOrganizationException extends RuntimeException{
    public AlreadyInOrganizationException() {
        super(ErrorMessages.ALREADY_IN_ORGANIZATION);
    }
}
