package com.rivalhub.event;

import java.util.List;

public interface EventService {
    EventDto addEvent(Long organizationId, EventDto eventDto);
    List<EventDto> findAllEvents(long id);
    EventDto findEvent(long eventId);
    boolean matchStrategy(String eventType);
}
