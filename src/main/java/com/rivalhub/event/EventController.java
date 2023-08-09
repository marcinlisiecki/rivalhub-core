package com.rivalhub.event;


import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class EventController {

    public final EventService eventService;
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> findEvent(@PathVariable long eventId, @PathParam("type") String type){
        return eventService.findEvent(eventId,type);
    }

    @PostMapping("/{id}/events/")
    public ResponseEntity<?> addEvent(@PathVariable long id, @RequestBody EventDto eventDto, @PathParam("type") String type){
        return eventService.addEvent(id, eventDto,type);
    }

    @GetMapping("/{id}/events/")
    public ResponseEntity<?> findAllEvents(@PathVariable long id,@PathParam("type") String type){
        return eventService.findAllEvents(id,type);
    }


}
