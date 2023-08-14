package com.rivalhub.reservation;

import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.station.StationDTO;
import com.rivalhub.user.UserDetailsDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ReservationDTO {
    private Long id;
    private List<StationDTO> stationList;
    private String startTime;
    private String endTime;
    private UserDetailsDto user;
    private OrganizationDTO organization;
}
