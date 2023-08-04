package com.rivalhub.organization;

import com.rivalhub.common.ErrorMessages;

public class WrongInvitationException extends RuntimeException {
    public WrongInvitationException() {
        super(ErrorMessages.WRONG_INVITATION);
    }
}
