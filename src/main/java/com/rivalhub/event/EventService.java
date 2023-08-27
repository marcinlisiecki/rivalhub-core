package com.rivalhub.event;

import com.rivalhub.user.UserDetailsDto;

import java.util.List;

public interface EventService {
    EventDto addEvent(Long organizationId, EventDto eventDto);

    List<EventDto> findAllEvents(long id);

    EventDto findEvent(long eventId);

    List<UserDetailsDto> findAllParticipants(long id);

    boolean matchStrategy(String eventType);

    List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId);

    List<UserDetailsDto> addUserToEvent(Long eventId, Long userId);

    void joinPublicEvent(Long id);
}
