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
         final  String PING_PONG = EventType.PING_PONG.getType();

        if(type.equals(EventType.PING_PONG.getType()))
            return pingPongService.findEvent(eventId);

        throw new InvalidPathParamException();
    }

    public EventDto addEvent(long id, EventDto eventDto, String type) {

        if(type.equals(EventType.PING_PONG.getType())) {
            EventDto savedEvent = pingPongService.addEvent(id, eventDto);
        }
        throw new InvalidPathParamException();
    }

    public List<EventDto> findAllEvents(long id, String type) {
        List<EventDto> eventDtoList = new ArrayList<>();
        if(type.equals(EventType.ALL.getType())) {
            eventDtoList.addAll(pingPongService.findAllEvents(id));
            //Dodać reszte
            return eventDtoList;
        }
        if(type.equals(EventType.PING_PONG.getType())) {
                eventDtoList.addAll(pingPongService.findAllEvents(id));
                return eventDtoList;
        }
        throw  new InvalidPathParamException();
    }
}
