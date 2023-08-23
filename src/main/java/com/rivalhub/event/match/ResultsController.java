package com.rivalhub.event.match;

import com.rivalhub.event.darts.match.DartMatch;
import com.rivalhub.event.darts.match.DartMatchService;
import com.rivalhub.event.darts.match.result.Leg;
import com.rivalhub.event.darts.match.result.LegAddDto;
import com.rivalhub.event.pingpong.match.PingPongMatchService;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.event.pullups.match.PullUpMatchService;
import com.rivalhub.event.tablefootball.match.TableFootballMatchService;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/{eventId}/match")
public class ResultsController {
    private final PingPongMatchService pingPongMatchService;
    private final DartMatchService dartMatchService;
    private final TableFootballMatchService tableFootballMatchService;
    private final PullUpMatchService pullUpMatchService;
    @PostMapping("/{matchId}/pingpong")
    private ResponseEntity<?> addResultsPingPong(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<PingPongSet> setList) {
        return ResponseEntity.ok(pingPongMatchService.addResult(eventId, matchId, setList));
    }

    @PostMapping("/{matchId}/tablefootball")
    private ResponseEntity<?> addResultsTableFootball(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<TableFootballMatchSet> setList) {
        return ResponseEntity.ok(tableFootballMatchService.addResult(eventId, matchId, setList));
    }


    @PostMapping("/{matchId}/pullups")
    private ResponseEntity<?> addResultsPullUps(@PathVariable Long eventId,
                                                      @PathVariable Long matchId,
                                                      @RequestBody List<TableFootballMatchSet> setList) {
        return ResponseEntity.ok(tableFootballMatchService.addResult(eventId, matchId, setList));
    }


    @PostMapping("/{matchId}/dart")
    private ResponseEntity<?> addResultsDart(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<LegAddDto> legListDto) {
        return ResponseEntity.ok(dartMatchService.addResult(eventId, matchId, legListDto));
    }
}
