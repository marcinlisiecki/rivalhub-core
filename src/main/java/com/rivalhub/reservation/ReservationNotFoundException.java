package com.rivalhub.reservation;

import com.rivalhub.common.ErrorMessages;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(){ super(ErrorMessages.RESERVATION_NOT_FOUND);}
}
