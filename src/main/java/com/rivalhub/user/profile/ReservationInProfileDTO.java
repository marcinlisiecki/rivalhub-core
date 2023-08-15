package com.rivalhub.user.profile;

import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.station.StationDTO;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class ReservationInProfileDTO {
    private Long id;
    private List<StationDTO> stationList;
    private String startTime;
    private String endTime;
    private UserDetailsDto user;
    private OrganizationDTO organization;
}
