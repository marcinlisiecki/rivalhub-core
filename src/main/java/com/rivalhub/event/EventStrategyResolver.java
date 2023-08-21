package com.rivalhub.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventStrategyResolver {

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
