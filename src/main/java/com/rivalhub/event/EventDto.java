package com.rivalhub.event;


import com.rivalhub.organization.OrganizationDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class EventDto {
    private Long eventId;
    private List<Long> stationList;
    private String startTime;
    private String endTime;
    private Long host;
    private List<Long> participants;
    private OrganizationDTO organization;
    private EventType eventType;
    private String name;
    private String description;
    private boolean isEventPublic;
    private String status;
    private double distance;
    private Long reservationId;


    public boolean isEventPublic() {
        return isEventPublic;
    }

    public void setIsEventPublic(boolean eventPublic) {
        isEventPublic = eventPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDto eventDto)) return false;
        return Objects.equals(eventId, eventDto.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
