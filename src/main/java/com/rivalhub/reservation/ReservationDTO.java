package com.rivalhub.reservation;

import com.rivalhub.station.Station;
import com.rivalhub.user.UserDetailsDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ReservationDTO {
    private List<Station> stationList;
    private String startTime;
    private String endTime;
    private UserDetailsDto user;
}
