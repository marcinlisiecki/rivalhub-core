package com.rivalhub.user.profile;

import com.rivalhub.event.EventType;
import com.rivalhub.organization.OrganizationDTO;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class EventProfileDTO {
    private Long eventId;
    private List<Long> stationList;
    private String startTime;
    private String endTime;
    private OrganizationDTO organization;
    private EventType eventType;
    private Long numberOfParticipants;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventProfileDTO that)) return false;
        return Objects.equals(eventId, that.eventId) && eventType == that.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType);
    }
}
