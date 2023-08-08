package com.rivalhub.reservation;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class AddReservationDTO {
    private List<Long> stationsIdList;
    private String startTime;
    private String endTime;
}
