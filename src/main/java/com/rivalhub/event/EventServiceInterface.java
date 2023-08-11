package com.rivalhub.event;

import java.util.List;

public interface EventServiceInterface {
    public EventDto addEvent(Long organizationId, EventDto eventDto);
    public List<EventDto> findAllEvents(long id);
}
