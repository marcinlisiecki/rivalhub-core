package com.rivalhub.event;


import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequiredArgsConstructor
public class EventController {

    public final EventService eventService;
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> findEvent(@PathVariable long eventId, @PathParam("type") String type){
        return ResponseEntity.ok(eventService.findEvent(eventId,type));
    }

    @PostMapping("/{id}/events/")
    public ResponseEntity<?> addEvent(@PathVariable long id, @RequestBody EventDto eventDto, @PathParam("type") String type){
        EventDto savedEvent = eventService.addEvent(id,eventDto,type);
        URI savedEventUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/events/{eventId}")
                .queryParam("type", type)
                .buildAndExpand(savedEvent.getEventId())
                .toUri();
        return ResponseEntity.created(savedEventUri).build();
    }

    @GetMapping("/{id}/events/")
    public ResponseEntity<?> findAllEvents(@PathVariable long id,@PathParam("type") String type){
        return ResponseEntity.ok(eventService.findAllEvents(id,type));
    }


}
