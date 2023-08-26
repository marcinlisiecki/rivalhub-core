package com.rivalhub.event;

import com.rivalhub.user.UserDetailsDto;

import java.util.List;

public interface EventService {
    EventDto addEvent(Long organizationId, EventDto eventDto);
    List<EventDto> findAllEvents(long id);
    EventDto findEvent(long eventId);
    List<UserDetailsDto> findAllParticipants(long id);
    boolean matchStrategy(String eventType);

    void joinPublicEvent(Long id);
}
