package com.rivalhub.event.darts.match.result;

import com.rivalhub.event.darts.DartEvent;
import org.springframework.stereotype.Component;

@Component
public class DartResultMapper {
    public Leg map(LegAddDto legAddDto){
        Leg leg = new Leg();
        leg.setRoundList(legAddDto.getRoundList().stream().map(this::map).toList());
        return leg;
    }

    public DartRound map(DartRoundDto dartRoundDto){
        DartRound dartRound = new DartRound();
        dartRound.setSinglePlayerScoreInRoundsList(dartRoundDto.getSinglePlayerScoreInRoundsList()
                .stream()
                .map(this::map)
                .toList());
        return dartRound;
    }

    SinglePlayerScoreInRound map(SinglePlayerScoreInRoundDto singlePlayerScoreInRoundDto){


        SinglePlayerScoreInRound singlePlayerScoreInRound = new SinglePlayerScoreInRound();
        singlePlayerScoreInRound.setScore(singlePlayerScoreInRoundDto.getScore());
        singlePlayerScoreInRound.setBlanks(singlePlayerScoreInRoundDto.getBlanks());
        return singlePlayerScoreInRound;
    }
}
