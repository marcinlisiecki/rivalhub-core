package com.rivalhub.event;


import com.rivalhub.organization.OrganizationDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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


    public boolean isEventPublic() {
        return isEventPublic;
    }

    public void setIsEventPublic(boolean eventPublic) {
        isEventPublic = eventPublic;
    }
}
