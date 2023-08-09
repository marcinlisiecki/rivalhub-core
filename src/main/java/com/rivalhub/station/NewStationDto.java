package com.rivalhub.station;

import com.rivalhub.event.EventType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewStationDto {
    private Long id;
    private EventType type;
    private String name;
}
