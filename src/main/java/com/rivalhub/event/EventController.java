package com.rivalhub.event;

import com.rivalhub.event.running.RunningEventService;
import com.rivalhub.event.running.UserTimesAddDto;
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
    private final RunningEventService runningEventService;

    @GetMapping("/events/{eventId}")
    private ResponseEntity<?> findEvent(@PathVariable Long eventId, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.findEvent(eventId, type));
    }

    @DeleteMapping("/events/{eventId}/participants")
    private ResponseEntity<?> deleteUserFromEvent(@PathVariable Long eventId, @RequestBody Long userId, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.deleteUserFromEvent(eventId, userId, type));
    }

    @PostMapping("/events/{eventId}/participants")
    private ResponseEntity<?> addUserToEvent(@PathVariable Long eventId, @RequestBody Long userId, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.addUserToEvent(eventId, userId, type));
    }

    @DeleteMapping ("{organizationId}/events/{eventId}")
    private ResponseEntity<?> deleteEvent(@PathVariable Long organizationId, @PathVariable Long eventId, @RequestParam String type) {
        eventStrategyResolver.deleteEvent(organizationId,eventId,type);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/{eventId}/participants")
    ResponseEntity<List<UserDetailsDto>> findEventParticipants(@PathVariable Long eventId, @RequestParam String type) {
        return ResponseEntity.ok(eventStrategyResolver.findEventParticipants(eventId, type));
    }

    @PostMapping("/{id}/events")
    private ResponseEntity<?> addEvent(@PathVariable Long id, @RequestBody EventDto eventDto,
                                       @RequestParam String type) {
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

    @PostMapping("/events/{eventId}/running")
    private ResponseEntity<?> addRunningResults(@PathVariable Long eventId,@RequestBody List<UserTimesAddDto> userTimesList) {
        return ResponseEntity.ok(runningEventService.addRunningResults(eventId,userTimesList));
    }

    @GetMapping("/events/{eventId}/running")
    private ResponseEntity<?> getRunningResults(@PathVariable Long eventId) {
        return ResponseEntity.ok(runningEventService.getRunningResults(eventId));
    }


}
