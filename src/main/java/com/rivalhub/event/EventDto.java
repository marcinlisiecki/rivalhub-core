package com.rivalhub.event;


import com.rivalhub.organization.OrganizationDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    Long eventId;
    List<Long> stationList;
    String startTime;
    String endTime;
    Long host;
    List<Long> participants;
    OrganizationDTO organization;
}
