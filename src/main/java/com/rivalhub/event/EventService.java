package com.rivalhub.event;

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

    public ResponseEntity<?> findEvent(long eventId, String type) {
        switch (type){
            case "PING_PONG":
                return ResponseEntity.ok(pingPongService.findEvent(eventId));
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<?> addEvent(long id, EventDto eventDto, String type) {
        switch (type){
            case "PING_PONG":
                EventDto savedEvent = pingPongService.addEvent(id, eventDto);
                URI savedEventUri = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/events/{eventId}")
                        .queryParam("type","PING_PONG")
                        .buildAndExpand(savedEvent.getEventId())
                        .toUri();
                return ResponseEntity.created(savedEventUri).build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<?> findAllEvents(long id, String type) {
        List<EventDto> eventDtoList = new ArrayList<>();
        switch (type)
        {
            case "ALL":

                eventDtoList.addAll(pingPongService.findAllEvents(id));
                //Dodać reszte
                return ResponseEntity.ok(eventDtoList);
            case "PING_PONG":
                eventDtoList.addAll(pingPongService.findAllEvents(id));
                return ResponseEntity.ok(eventDtoList);

        }
        return ResponseEntity.badRequest().build();
    }
}
