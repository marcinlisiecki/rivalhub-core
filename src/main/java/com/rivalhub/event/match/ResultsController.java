package com.rivalhub.event.match;

import com.rivalhub.event.pingpong.match.PingPongMatchService;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/{eventId}/match")
public class ResultsController {
    private final PingPongMatchService pingPongMatchService;
    @PostMapping("/{matchId}/pingpong")
    private ResponseEntity<?> addResults(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestParam String type,
                                         @RequestBody List<PingPongSet> setList) {
        return ResponseEntity.ok(pingPongMatchService.addResult(eventId, matchId, setList));
    }
}
