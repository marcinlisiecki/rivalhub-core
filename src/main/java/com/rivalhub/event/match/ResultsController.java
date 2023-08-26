package com.rivalhub.event.match;

import com.rivalhub.event.billiards.BilliardsService;
import com.rivalhub.event.billiards.match.BilliardsMatch;
import com.rivalhub.event.billiards.match.BilliardsMatchResultAdd;
import com.rivalhub.event.billiards.match.BilliardsMatchService;
import com.rivalhub.event.darts.match.DartMatchService;
import com.rivalhub.event.darts.match.result.LegAddDto;
import com.rivalhub.event.pingpong.match.PingPongMatchService;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.event.pullups.match.PullUpMatchService;
import com.rivalhub.event.pullups.match.result.PullUpSeries;
import com.rivalhub.event.pullups.match.result.PullUpSeriesAddDto;
import com.rivalhub.event.pullups.match.result.PullUpSeriesDto;
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
    private final BilliardsMatchService billiardsMatchService;
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
                                                      @RequestBody List<PullUpSeriesAddDto> pullUpSeries) {
        return ResponseEntity.ok(pullUpMatchService.addResult(eventId, matchId, pullUpSeries));
    }


    @PostMapping("/{matchId}/dart")
    private ResponseEntity<?> addResultsDart(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<LegAddDto> legListDto) {
        return ResponseEntity.ok(dartMatchService.addResult(eventId, matchId, legListDto));
    }

    @PostMapping("/{matchId}/billiards")
    private ResponseEntity<?> addResultsBilliards(@PathVariable Long eventId,
                                             @PathVariable Long matchId,
                                             @RequestBody BilliardsMatchResultAdd billiardsMatchResultAdd) {
        return ResponseEntity.ok(billiardsMatchService.addResult(eventId, matchId, billiardsMatchResultAdd));
    }
}
