package com.rivalhub.event;

import com.rivalhub.common.exception.InvalidPathParamException;
import com.rivalhub.event.pingpong.PingPongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventOperator eventOperator;


    EventDto findEvent(Long eventId, String type) {
        return eventOperator.useStrategy(type).findEvent(eventId);
    }

    EventDto addEvent(Long id, EventDto eventDto, String type) {
        return eventOperator.useStrategy(type).addEvent(id,eventDto);
    }

    List<EventDto> findAllEvents(Long id, String type) {
        return eventOperator.useStrategy(type).findAllEvents(id);
    }
}
