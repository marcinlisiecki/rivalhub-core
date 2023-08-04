package com.rivalhub.reservation;

import lombok.Getter;

import java.util.List;

@Getter
public class AddReservationDTO {

    private List<Long> stationsIdList;
    private String startTime;
    private String endTime;
}
