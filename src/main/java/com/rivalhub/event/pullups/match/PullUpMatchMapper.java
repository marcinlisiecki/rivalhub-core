package com.rivalhub.event.pullups.match;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.pullups.match.result.PullUpScore;
import com.rivalhub.event.pullups.match.result.ViewPullUpMatchDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PullUpMatchMapper {
    private final AutoMapper autoMapper;

    PullUpMatch map(MatchDto matchDto, Organization organization){
        PullUpMatch pullUpMatch = new PullUpMatch();

        List<UserData> participants = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam1Ids()))
                .toList();


        pullUpMatch.setParticipants(participants);
        return pullUpMatch;
    }

    MatchDto mapToMatchDto(PullUpMatch pullUpMatch){
        MatchDto pullUpMatchDTO = new MatchDto();

        List<UserDetailsDto> team1 = pullUpMatch.getParticipants()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();


        pullUpMatchDTO.setId(pullUpMatch.getId());
        pullUpMatchDTO.setTeam1Ids(team1.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        pullUpMatchDTO.setTeam1Approval(pullUpMatch.isApprovalFirstPlace());
        pullUpMatchDTO.setTeam2Approval(pullUpMatch.isApprovalSecondPlace());
        pullUpMatchDTO.setTeam3Approval(pullUpMatch.isApprovalThirdPlace());

        return pullUpMatchDTO;
    }


    ViewPullUpMatchDto map(PullUpMatch pullUpMatch){
        ViewPullUpMatchDto viewPullUpMatchDto = new ViewPullUpMatchDto();

        List<UserDetailsDto> team1 = pullUpMatch.getParticipants()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();



        viewPullUpMatchDto.setUserDetailsDtos(team1);
        viewPullUpMatchDto.setScores(getScores(pullUpMatch));

        viewPullUpMatchDto.setPlaces(getPlaces(viewPullUpMatchDto.getScores()));


        return viewPullUpMatchDto;
    }


    List<List<Long>> getScores(PullUpMatch pullUpMatch){
        List<List<Long>> series = new ArrayList<>();

        for(int round = 0 ; round < pullUpMatch.getPullUpSeries().size(); round++ ){
             List<Long> scores = pullUpMatch.getPullUpSeries()
                     .get(round)
                     .getPullUpScoreList()
                     .stream()
                     .map(PullUpScore::getScore)
                     .toList();
             series.add(scores);
        }

        return series;
    };

    List<Integer> getPlaces(List<List<Long>> scores) {
        int numberOfParticipants = scores.get(0).size();
        List<Integer> places = Arrays.asList(new Integer[numberOfParticipants]);
        List<Integer> scoreSortedIndexes = Arrays.asList(new Integer[numberOfParticipants]);
        List<Long> overallScore = Arrays.asList(new Long[numberOfParticipants]);


        for (int playerNumber = 0;playerNumber<numberOfParticipants;playerNumber++) {
            overallScore.set(playerNumber,0L);
            scoreSortedIndexes.set(playerNumber, playerNumber);
        }

        for(int round = 0; round < scores.size();round++) {
            for(int playerNumber = 0;playerNumber<numberOfParticipants;playerNumber++){
                overallScore.set(playerNumber,overallScore.get(playerNumber)+scores.get(round).get(playerNumber));
            }
        }
        Collections.sort(scoreSortedIndexes, Comparator.comparingLong(overallScore::get));
        for (int playerNumber = 0;playerNumber<numberOfParticipants;playerNumber++) {
            overallScore.
        }

        return scoreSortedIndexes;
    }

    public
}
