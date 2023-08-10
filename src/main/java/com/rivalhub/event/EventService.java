package com.rivalhub.event;

import com.rivalhub.common.exception.InvalidPathParamException;
import com.rivalhub.email.EmailService;
import com.rivalhub.event.pingpong.PingPongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {

    final PingPongService pingPongService;
    public EventDto findEvent(long eventId, String type) {
        if(type.equals(EventType.PING_PONG.name()))
            return pingPongService.findEvent(eventId);

        throw new InvalidPathParamException();
    }

    public EventDto addEvent(long id, EventDto eventDto, String type) {

        if(type.equals(EventType.PING_PONG.name())) {
            EventDto savedEvent = pingPongService.addEvent(id, eventDto);
            return savedEvent;
        }
        throw new InvalidPathParamException();
    }

    public List<EventDto> findAllEvents(long id, String type) {
        List<EventDto> eventDtoList = new ArrayList<>();
        if(type.equals(EventType.ALL.name())) {
            eventDtoList.addAll(pingPongService.findAllEvents(id));
            //Dodać reszte
            return eventDtoList;
        }
        if(type.equals(EventType.PING_PONG.name())) {
                eventDtoList.addAll(pingPongService.findAllEvents(id));
                return eventDtoList;
        }
        throw  new InvalidPathParamException();
    }
}
