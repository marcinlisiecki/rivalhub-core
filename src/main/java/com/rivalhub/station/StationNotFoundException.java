package com.rivalhub.station;

import com.rivalhub.common.ErrorMessages;

public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException() {
        super(ErrorMessages.STATION_NOT_FOUND);
    }
}
