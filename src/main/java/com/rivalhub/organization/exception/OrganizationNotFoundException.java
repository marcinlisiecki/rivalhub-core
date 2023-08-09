package com.rivalhub.organization.exception;

import com.rivalhub.common.ErrorMessages;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException() {
        super(ErrorMessages.ORGANIZATION_NOT_FOUND);
    }
}
