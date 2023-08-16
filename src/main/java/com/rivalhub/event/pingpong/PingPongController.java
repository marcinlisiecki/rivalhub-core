package com.rivalhub.event.pingpong;

import com.rivalhub.event.pingpong.match.AddPingPongMatchDTO;
import com.rivalhub.event.pingpong.match.PingPongMatchService;
import com.rivalhub.event.pingpong.match.PingPongSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/{eventId}/match")
public class PingPongController {

    private final PingPongMatchService pingPongMatchService;

    @PostMapping("")
    private ResponseEntity<?> createPingPongMatch(@PathVariable Long organizationId,
                                                  @PathVariable Long eventId,
                                                  @RequestBody AddPingPongMatchDTO pingPongMatchDTO) {
        return ResponseEntity.ok(pingPongMatchService.createPingPongMatch(eventId, organizationId, pingPongMatchDTO));
    }

    @PostMapping("/{matchId}")
    private ResponseEntity<?> addResults(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<PingPongSet> setList) {
        return ResponseEntity.ok(pingPongMatchService.addResult(eventId, matchId, setList));
    }

    @GetMapping("/{matchId}/approve")
    private ResponseEntity<?> setResultApproval(@PathVariable Long eventId,
                                                @PathVariable Long matchId,
                                                @RequestParam boolean approve) {
        return ResponseEntity.ok(pingPongMatchService.setResultApproval(eventId, matchId, approve));
    }

    @GetMapping("/{matchId}")
    private ResponseEntity<?> getPingPongMatch(@PathVariable Long eventId,
                                               @PathVariable Long matchId) {
        return ResponseEntity.ok(pingPongMatchService.findPingPongMatch(eventId, matchId));
    }

    @GetMapping("")
    private ResponseEntity<?> getPingPongMatches(@PathVariable Long eventId) {
        return ResponseEntity.ok(pingPongMatchService.findPingPongMatches(eventId));
    }
}
