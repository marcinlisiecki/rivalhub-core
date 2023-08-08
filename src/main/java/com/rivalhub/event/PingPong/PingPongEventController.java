package com.rivalhub.event.PingPong;

import com.rivalhub.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PingPongEventController {

    final PingPongService pingPongService;
    @PostMapping("/{id}/events/pingpong")
    public ResponseEntity<?> addPingPongEvent(@PathVariable long id, @RequestBody EventDto eventDto){
        pingPongService.addEvent(eventDto);
        return ResponseEntity.ok().build();
    }

}
