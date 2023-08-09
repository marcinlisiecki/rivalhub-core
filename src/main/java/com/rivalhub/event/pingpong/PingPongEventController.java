package com.rivalhub.event.pingpong;

import com.rivalhub.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PingPongEventController {

    final PingPongService pingPongService;
    @PostMapping("/{id}/events/pingpong")
    public ResponseEntity<?> addPingPongEvent(@PathVariable long id, @RequestBody EventDto eventDto){
        pingPongService.addEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/events/pingpong")
    public ResponseEntity<?> findAllPingPongEvents(@PathVariable long id){
        pingPongService.findAllPingPongEvents(eventDto);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/{id}/events/pingpong/{id}")
//    public ResponseEntity<?> addPingPongEvent(@PathVariable long id, @RequestBody EventDto eventDto){
//        pingPongService.addEvent(eventDto);
//        return ResponseEntity.ok().build();
//    }
}
