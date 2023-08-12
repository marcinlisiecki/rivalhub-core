package com.rivalhub.event.pingpong;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/")
public class PingPongController {

    private final PingPongMatchService pingPongMatchService;

    @PostMapping("{eventId}/match")
    ResponseEntity<?> createPingPongMatch(@PathVariable Long organizationId,
                                          @PathVariable Long eventId,
                                          @RequestBody PingPongMatchDTO pingPongMatchDTO,
                                          @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(pingPongMatchService.createPingPongMatch(organizationId, eventId, userDetails.getUsername(), pingPongMatchDTO));
    }
}
