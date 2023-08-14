package com.rivalhub.event.pingpong;

import com.rivalhub.event.pingpong.match.AddPingPongMatchDTO;
import com.rivalhub.event.pingpong.match.PingPongMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/{eventId}/match")
public class PingPongController {

    private final PingPongMatchService pingPongMatchService;

    @PostMapping("")
    private ResponseEntity<?> createPingPongMatch(@PathVariable Long organizationId,
                                          @PathVariable Long eventId,
                                          @RequestBody AddPingPongMatchDTO pingPongMatchDTO,
                                          @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(pingPongMatchService.createPingPongMatch(organizationId, eventId, userDetails.getUsername(), pingPongMatchDTO));
    }

    @GetMapping("/{matchId}/approve")
    private ResponseEntity<?> setResultApproval(@PathVariable Long organizationId,
                                             @PathVariable Long eventId,
                                             @PathVariable Long matchId,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam boolean approve){
        return ResponseEntity.ok(pingPongMatchService.setResultApproval(organizationId, eventId, matchId, userDetails.getUsername(), approve));
    }

    @GetMapping("/{matchId}")
    private ResponseEntity<?> getPingPongMatch(@PathVariable Long organizationId,
                                               @PathVariable Long eventId,
                                               @PathVariable Long matchId,
                                               @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(pingPongMatchService.findPingPongMatch(organizationId, eventId, matchId, userDetails.getUsername()));
    }


}
