package com.rivalhub.event;

import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
public class EventController {

    private final EventStrategyResolver eventStrategyResolver;

    @GetMapping("/events/{eventId}")
    private ResponseEntity<?> findEvent(@PathVariable Long eventId, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.findEvent(eventId, type));
    }

    @GetMapping("/events/{eventId}/participants")
    ResponseEntity<List<UserDetailsDto>> findEventParticipants(@PathVariable Long eventId, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.findEventParticipants(eventId, type));
    }

    @PostMapping("/{id}/events")
    private ResponseEntity<?> addEvent(@PathVariable Long id, @RequestBody EventDto eventDto,
                                       @RequestParam(name = "type") String type) {
        EventDto savedEvent = eventStrategyResolver.addEvent(id, eventDto, type);
        URI savedEventUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/events/{eventId}")
                .queryParam("type", type)
                .buildAndExpand(savedEvent.getEventId())
                .toUri();
        return ResponseEntity.created(savedEventUri).build();
    }

    @GetMapping("/{id}/events")
    private ResponseEntity<?> findAllEvents(@PathVariable Long id, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.findAllEvents(id, type));
    }

    @GetMapping("/{id}/events/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void joinPublicEvent(@PathVariable Long id, @RequestParam String type) {
        eventStrategyResolver.joinPublicEvent(id, type);
    }
}