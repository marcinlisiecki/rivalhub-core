package com.rivalhub.common.exception;

import com.rivalhub.common.ErrorMessages;

public class ReservationIsNotPossible extends RuntimeException{
    public ReservationIsNotPossible() {
        super(ErrorMessages.RESERVATION_IS_NOT_POSSIBLE);
    }

}
