package com.rivalhub.event;

import java.util.List;

public interface EventServiceInterface {
    EventDto addEvent(Long organizationId, EventDto eventDto);
    List<EventDto> findAllEvents(long id);
    EventDto findEvent(long eventId);
    EventType getEventType();
}
