package com.rivalhub.event.pingpong;

import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PingPongEventController {

    final PingPongService pingPongService;
    @PostMapping("/{id}/events/pingpong")
    public ResponseEntity<?> addPingPongEvent(@PathVariable long id, @RequestBody EventDto eventDto){
        EventDto savedEvent = pingPongService.addEvent(id, eventDto);
        URI savedEventUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/events/pingpong/{eventId}")
                .buildAndExpand(savedEvent.getEventId())
                .toUri();
        return ResponseEntity.created(savedEventUri).build();
    }

    @GetMapping("/{id}/events/pingpong")
    public ResponseEntity<?> findAllPingPongEvents(@PathVariable long id){
        List<EventDto> eventDtoList = pingPongService.findAllEvents(id);
        return ResponseEntity.ok(eventDtoList);
    }

    @GetMapping("/events/pingpong/{eventId}")
    public ResponseEntity<?> findPingPongEvent(@PathVariable long eventId){

        return ResponseEntity.ok(pingPongService.findEvent(eventId));
    }
}
