package com.rivalhub.user.profile;

import com.rivalhub.event.EventType;
import com.rivalhub.organization.OrganizationDTO;
import lombok.Data;

import java.util.List;

@Data
public class EventProfileDTO {
    private Long eventId;
    private List<Long> stationList;
    private String startTime;
    private String endTime;
    private OrganizationDTO organization;
    private EventType eventType;
    private Long numberOfParticipants;
}
