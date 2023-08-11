package com.rivalhub.event;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventDto {
    Long eventId;
    List<Long> stationList;
    String startTime;
    String endTime;
    Long host;
    List<Long> participants;
    //OrganizationDTO organization;
}
