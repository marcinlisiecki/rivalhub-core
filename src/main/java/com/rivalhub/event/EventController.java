package com.rivalhub.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
public class EventController {

    private final EventService eventService;

    @GetMapping("/events/{eventId}")
    private ResponseEntity<?> findEvent(@PathVariable Long eventId, @RequestParam(name = "type") String type) {
        return ResponseEntity.ok(eventService.findEvent(eventId, type));
    }

    @PostMapping("/{id}/events")
    private ResponseEntity<?> addEvent(@PathVariable Long id, @RequestBody EventDto eventDto,
                                       @RequestParam(name = "type") String type) {
        EventDto savedEvent = eventService.addEvent(id, eventDto, type);
        URI savedEventUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/events/{eventId}")
                .queryParam("type", type)
                .buildAndExpand(savedEvent.getEventId())
                .toUri();
        return ResponseEntity.created(savedEventUri).build();
    }

    @GetMapping("/{id}/events")
    private ResponseEntity<?> findAllEvents(@PathVariable Long id, @RequestParam(name = "type") String type) {
        return ResponseEntity.ok(eventService.findAllEvents(id, type));
    }
}
