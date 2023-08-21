package com.rivalhub.event.match;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/{eventId}/match")
public class MatchController {

    private final MatchService matchService;

    @PostMapping("")
    private ResponseEntity<?> createMatch(@PathVariable Long organizationId,
                                                  @PathVariable Long eventId,
                                                  @RequestParam String type,
                                                  @RequestBody MatchDto matchDTO) {
        return ResponseEntity.ok(matchService.createMatch(organizationId,eventId, matchDTO,type));
    }



    @GetMapping("/{matchId}/approve")
    private ResponseEntity<?> setResultApproval(@PathVariable Long eventId,
                                                @PathVariable Long matchId,
                                                @RequestParam String type,
                                                @RequestParam boolean approve) {
        return ResponseEntity.ok(matchService.setResultApproval(eventId, matchId, approve,type));
    }

    @GetMapping("/{matchId}")
    private ResponseEntity<?> getPingPongMatch(@PathVariable Long eventId,
                                               @RequestParam String type,
                                               @PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.findMatch(eventId, matchId,type));
    }

    @GetMapping("")
    private ResponseEntity<?> getPingPongMatches(@PathVariable Long eventId,
                                                 @RequestParam String type)
    {
        return ResponseEntity.ok(matchService.findMatches(eventId,type));
    }
}
