package com.rivalhub.station;

import com.rivalhub.event.EventType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StationDTO {
    private Long id;
    private EventType type;
    private String name;
    private boolean isActive;
}
