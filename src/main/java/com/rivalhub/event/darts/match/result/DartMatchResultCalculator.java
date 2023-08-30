package com.rivalhub.event.darts.match.result;

import com.rivalhub.event.darts.match.DartMatch;
import com.rivalhub.event.darts.match.ViewDartMatchDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DartMatchResultCalculator {

    public void calculateResults(ViewDartMatchDto viewDartMatch, DartMatch dartMatch) {

        int maxScore = dartMatch.getDartFormat().maxScore;
        List<List<List<Long>>> scoresInMatch = new ArrayList<>();
        List<List<List<Integer>>> bounceOutInMatch = new ArrayList<>();
        List<List<List<Integer>>> pointLeft = new ArrayList<>();
        List<List<Integer>> pointsLeftInLeg = new ArrayList<>();
        List<List<Integer>> placesInLeg = new ArrayList<>();
        List<List<Integer>> bounceOutsInLeg = new ArrayList<>();
        List<List<Integer>> bestRoundScoresInLeg = new ArrayList<>();
        List<List<Integer>> numberOfRoundsPlayedInLeg = new ArrayList<>();

        for (int legNumber = 0; legNumber < dartMatch.getLegList().size(); legNumber++) {

            List<Integer> playersPointsLeft = new ArrayList<>();
            List<Integer> playersBlankShotsListInLeg = new ArrayList<>();
            List<Integer> playersHighestRoundScoreInLeg = new ArrayList<>();
            List<Integer> playersNumberOfThrows = new ArrayList<>();
            List<Integer> playerPlacesInLeg = Arrays.asList(new Integer[dartMatch.getParticipants().size()]);


            for (int i = 0; i < dartMatch.getParticipants().size(); i++) {
                playersPointsLeft.add(maxScore);
                playersBlankShotsListInLeg.add(0);
                playersHighestRoundScoreInLeg.add(0);
                playersNumberOfThrows.add(0);

            }

            List<List<Long>> roundList = new ArrayList<>();
            List<List<Integer>> bounceInLegList = new ArrayList<>();
            List<List<Integer>> leftInLeg = new ArrayList<>();
            int numberOfPlayersFinished = 0;
            for (int roundNumber = 0; roundNumber < dartMatch.getLegList().get(legNumber).getRoundList().size(); roundNumber++) {

                List<Long> scoresInRound = Arrays.asList(new Long[dartMatch.getParticipants().size()]);
                List<Integer> bounceInRound = Arrays.asList(new Integer[dartMatch.getParticipants().size()]);
                List<Integer> leftInRound = Arrays.asList(new Integer[dartMatch.getParticipants().size()]);

                int numberOfPlayersFinishedInRound = 0;

                for (int playerNumber = 0; playerNumber < dartMatch.getParticipants().size(); playerNumber++) {
                    leftInRound.set(playerNumber,playersPointsLeft.get(playerNumber));
                    if (playersPointsLeft.get(playerNumber) == 0)
                        continue;

                    SinglePlayerScoreInRound singlePlayerScoreInRound = dartMatch.getLegList().get(legNumber)
                            .getRoundList().get(roundNumber)
                            .getSinglePlayerScoreInRoundsList().get(playerNumber);


                    scoresInRound.set(playerNumber, singlePlayerScoreInRound.getScore());
                    bounceInRound.set(playerNumber, Math.toIntExact(singlePlayerScoreInRound.getBlanks()));
                    playersBlankShotsListInLeg.set(playerNumber, (int) (playersBlankShotsListInLeg.get(playerNumber) + singlePlayerScoreInRound.getBlanks()));

                    if (singlePlayerScoreInRound.getScore() > playersHighestRoundScoreInLeg.get(playerNumber)) {
                        playersHighestRoundScoreInLeg.set(playerNumber, Math.toIntExact(singlePlayerScoreInRound.getScore()));
                    }

                    playersPointsLeft.set(playerNumber, (int) (playersPointsLeft.get(playerNumber) - singlePlayerScoreInRound.getScore()));
                    playersNumberOfThrows.set(playerNumber, playersNumberOfThrows.get(playerNumber) + 1);

                    if (playersPointsLeft.get(playerNumber) == 0) {
                        playerPlacesInLeg.set(playerNumber, numberOfPlayersFinished + 1);
                        numberOfPlayersFinished++;
                    }

                }
                roundList.add(scoresInRound);
                bounceInLegList.add(bounceInRound);
                leftInLeg.add(leftInRound);
            }
            bounceOutInMatch.add(bounceInLegList);
            pointLeft.add(leftInLeg);
            scoresInMatch.add(roundList);
            Collections.replaceAll(playerPlacesInLeg, null, numberOfPlayersFinished + 1);
            pointsLeftInLeg.add(playersPointsLeft);
            placesInLeg.add(playerPlacesInLeg);
            bounceOutsInLeg.add(playersBlankShotsListInLeg);
            numberOfRoundsPlayedInLeg.add(playersNumberOfThrows);
            bestRoundScoresInLeg.add(playersHighestRoundScoreInLeg);

            viewDartMatch.setBounceOutsInLeg(bounceOutsInLeg);
            viewDartMatch.setPointsLeftInLeg(pointsLeftInLeg);
            viewDartMatch.setPlacesInLeg(placesInLeg);
            viewDartMatch.setBestRoundScoresInLeg(bestRoundScoresInLeg);
            viewDartMatch.setNumberOfRoundsPlayedInLeg(numberOfRoundsPlayedInLeg);
            viewDartMatch.setScoresInMatch(scoresInMatch);
            viewDartMatch.setBounceOutsInRound(bounceOutInMatch);
            viewDartMatch.setPointsLeftInLegAfterRound(pointLeft);
        }
    }
}
