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

    private final PingPongService pingPongService;

    EventDto findEvent(Long eventId, String type) {
        if (type.equals(EventType.PING_PONG.name()))
            return pingPongService.findEvent(eventId);

        throw new InvalidPathParamException();
    }

    EventDto addEvent(Long id, EventDto eventDto, String type) {
        if (type.equals(EventType.PING_PONG.name())) {
            return pingPongService.addEvent(id, eventDto);
        }
        throw new InvalidPathParamException();
    }

    List<EventDto> findAllEvents(Long id, String type) {
        List<EventDto> eventDtoList = new ArrayList<>();
        //TODO Dodać reszte
        if (type.equals(EventType.PING_PONG.name())) {
            eventDtoList.addAll(pingPongService.findAllEvents(id));
            return eventDtoList;
        }
        throw new InvalidPathParamException();
    }
}
