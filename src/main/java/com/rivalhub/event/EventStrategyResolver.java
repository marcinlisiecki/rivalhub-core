package com.rivalhub.event;

import com.rivalhub.user.UserDetailsDto;
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
        return eventOperator.useStrategy(type).addEvent(id, eventDto);
    }

    List<UserDetailsDto> findEventParticipants(long id, String type) {
        return eventOperator.useStrategy(type).findAllParticipants(id);
    }

    List<EventDto> findAllEvents(Long id, String type) {
        return eventOperator.useStrategy(type).findAllEvents(id);
    }

    List<UserDetailsDto> deleteUserFromEvent(Long eventId,Long userId, String type) {
        return eventOperator.useStrategy(type).deleteUserFromEvent(eventId,userId);
    }
    void joinPublicEvent(Long id, String type) {
        eventOperator.useStrategy(type).joinPublicEvent(id);
    }
}
