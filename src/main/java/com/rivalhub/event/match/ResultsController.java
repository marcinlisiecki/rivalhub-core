package com.rivalhub.event.match;

import com.rivalhub.event.billiards.BilliardsService;
import com.rivalhub.event.billiards.match.BilliardsMatch;
import com.rivalhub.event.billiards.match.BilliardsMatchResultAdd;
import com.rivalhub.event.billiards.match.BilliardsMatchService;
import com.rivalhub.event.darts.match.DartMatchService;
import com.rivalhub.event.darts.match.result.DartRoundDto;
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
import org.springframework.http.HttpStatus;
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

    @DeleteMapping("/{matchId}/pingpong")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deletePingPongSet(@PathVariable Long eventId,
                                                @PathVariable Long matchId,
                                                @RequestBody PingPongSet pingPongSet){
        pingPongMatchService.deletePingPongSet(eventId, matchId, pingPongSet);
    }

    @PatchMapping("/{matchId}/pingpong")
    private ResponseEntity<?> editPingPongSet(@PathVariable Long eventId,
                                              @PathVariable Long matchId,
                                              @RequestBody PingPongSet pingPongSet){
        return ResponseEntity.ok(pingPongMatchService.editPingPongSet(eventId, matchId, pingPongSet));
    }

    @PostMapping("/{matchId}/tablefootball")
    private ResponseEntity<?> addResultsTableFootball(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<TableFootballMatchSet> setList) {
        return ResponseEntity.ok(tableFootballMatchService.addResult(eventId, matchId, setList));
    }

    @DeleteMapping("/{matchId}/tablefootball")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteTableFootballSet(@PathVariable Long eventId,
                                                      @PathVariable Long matchId,
                                                      @RequestBody TableFootballMatchSet tableFootballSet) {
        tableFootballMatchService.deleteTableFootballSet(eventId, matchId, tableFootballSet);
    }

    @PostMapping("/{matchId}/pullups")
    private ResponseEntity<?> addResultsPullUps(@PathVariable Long eventId,
                                                      @PathVariable Long matchId,
                                                      @RequestBody List<PullUpSeriesAddDto> pullUpSeries) {
        return ResponseEntity.ok(pullUpMatchService.addResult(eventId, matchId, pullUpSeries));
    }

    @DeleteMapping("/{matchId}/pullups")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deletePullUpSeries(@PathVariable Long eventId,
                                                @PathVariable Long matchId,
                                                @RequestBody Long seriesId) {
        pullUpMatchService.deletePullUpSeries(eventId, matchId, seriesId);
    }

    @PostMapping("/{matchId}/dart")
    private ResponseEntity<?> addResultsDart(@PathVariable Long eventId,
                                         @PathVariable Long matchId,
                                         @RequestBody List<LegAddDto> legListDto) {
        return ResponseEntity.ok(dartMatchService.addResult(eventId, matchId, legListDto));
    }

    @PostMapping("/{matchId}/dart/legs")
    private ResponseEntity<?> createLeg(@PathVariable Long eventId,
                                             @PathVariable Long matchId) {
        return ResponseEntity.ok(dartMatchService.createLeg(eventId, matchId));
    }
    @PostMapping("/{matchId}/dart/legs/rounds/{legNumber}")
    private ResponseEntity<?> addRound(@PathVariable Long eventId,
                                        @PathVariable Long matchId,
                                       @PathVariable int legNumber,
                                       @RequestBody DartRoundDto dartRoundDto
    ) {
        return ResponseEntity.ok(dartMatchService.addRound(eventId, matchId,dartRoundDto,legNumber));
    }

    @DeleteMapping("/{matchId}/dart/legs/rounds/{legNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteLeg(@PathVariable Long eventId,
                                    @PathVariable Long matchId,
                                    @PathVariable Long legNumber,
                                    @RequestBody int numberOfRound
    ) {
        dartMatchService.deleteRound(matchId, legNumber,numberOfRound);
    }


    @PostMapping("/{matchId}/billiards")
    private ResponseEntity<?> addResultsBilliards(@PathVariable Long eventId,
                                             @PathVariable Long matchId,
                                             @RequestBody BilliardsMatchResultAdd billiardsMatchResultAdd) {
        return ResponseEntity.ok(billiardsMatchService.addResult(eventId, matchId, billiardsMatchResultAdd));
    }
}
