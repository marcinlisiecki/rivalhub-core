package com.rivalhub.event.match;

import com.rivalhub.event.pingpong.match.PingPongMatchService;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.event.tablefootball.match.TableFootballMatchService;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//TODO Wydaje mi się że można wbić tą klase do
// matchcontrolera za pomocą zewnętrznego interfejsu na każdy
// z wyników i customowego deserializatora gdy nie bedzie co robic
// refactor
@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/{organizationId}/events/{eventId}/match")
public class ResultsController {
    private final PingPongMatchService pingPongMatchService;
    private final TableFootballMatchService tableFootballMatchService;
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


}
