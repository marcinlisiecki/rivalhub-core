package com.rivalhub.event;

import com.rivalhub.event.running.RunningEventService;
import com.rivalhub.event.running.UserTimesAddDto;
import lombok.RequiredArgsConstructor;
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
    private final RunningEventService runningEventService;

    @GetMapping("/events/{eventId}")
    private ResponseEntity<?> findEvent(@PathVariable Long eventId, @RequestParam(name = "type") String type) {
        return ResponseEntity.ok(eventStrategyResolver.findEvent(eventId, type));
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
    private ResponseEntity<?> findAllEvents(@PathVariable Long id, @RequestParam(name = "type") String type) {
        return ResponseEntity.ok(eventStrategyResolver.findAllEvents(id, type));
    }

    @PostMapping("/events/{eventId}/running")
    private ResponseEntity<?> addRunningResults(@PathVariable Long eventId,@RequestBody List<UserTimesAddDto> userTimesList) {
        return ResponseEntity.ok(runningEventService.addRunningResults(eventId,userTimesList));
    }

    @GetMapping("/events/{eventId}/running")
    private ResponseEntity<?> getRunningResults(@PathVariable Long eventId) {
        return ResponseEntity.ok(runningEventService.getRunningResults(eventId));
    }
}
