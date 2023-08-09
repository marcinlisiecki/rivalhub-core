package com.rivalhub.station;

import com.rivalhub.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventTypeStationsDto {

    private EventType type;
    private LocalDateTime firstAvailable;
    private List<StationDTO> stations;
}
