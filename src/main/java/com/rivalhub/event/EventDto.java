package com.rivalhub.event;


import com.rivalhub.organization.OrganizationDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    EventType eventType;
}
