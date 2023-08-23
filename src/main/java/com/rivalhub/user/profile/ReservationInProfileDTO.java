package com.rivalhub.user.profile;

import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.station.StationDTO;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class ReservationInProfileDTO {
    private Long id;
    private List<StationDTO> stationList;
    private String startTime;
    private String endTime;
    private UserDetailsDto user;
    private OrganizationDTO organization;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationInProfileDTO that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
