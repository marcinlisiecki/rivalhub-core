package com.rivalhub.event.pullups.match;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.pullups.match.result.*;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PullUpMatchMapper {
    private final AutoMapper autoMapper;
    private final PullUpResultMapper pullUpResultMapper;

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

        viewPullUpMatchDto.setScores(pullUpMatch.getPullUpSeries().stream().map(pullUpResultMapper::map).toList());
        viewPullUpMatchDto.setPlaces(getPlaces(pullUpMatch));

        return viewPullUpMatchDto;
    }

    Map<Long,Integer> getPlaces(PullUpMatch pullUpMatch) {
        Map<Long, Integer> overalScoreMap = new HashMap<>();
        Map<Long, Integer> placesMap = new HashMap<>();
        List<SingleUserScore> singleUserScoreList = pullUpMatch.getPullUpSeries()
                .stream()
                .map(this::getScore)
                .toList();
        pullUpMatch.getParticipants()
                .forEach(participant -> overalScoreMap.put(participant.getId(), 0));
        singleUserScoreList
                .forEach(score-> overalScoreMap.put(score.getId(),score.getScore()+ overalScoreMap.get(score.getId())));

        int numberOfPlayersWithAssignedPlaces = 0;
        for(int iteration = 0; iteration < pullUpMatch.getParticipants().size(); iteration++){
            int numberOfPlayersWithAssignedPlaceInThisIteration = 0;
            int highestScore = 0;
            List<Long> IdOfUsersThatGonnaHaveAssignedPlaceInThisIteration = new ArrayList<>();
            for(Long key: overalScoreMap.keySet()){
                if(highestScore == overalScoreMap.get(key)){
                    IdOfUsersThatGonnaHaveAssignedPlaceInThisIteration.add(key);
                    continue;
                }
                if(highestScore < overalScoreMap.get(key)){
                    IdOfUsersThatGonnaHaveAssignedPlaceInThisIteration.clear();
                    IdOfUsersThatGonnaHaveAssignedPlaceInThisIteration.add(key);
                    highestScore = overalScoreMap.get(key);
                }
            }
            for (Long idOfUser: IdOfUsersThatGonnaHaveAssignedPlaceInThisIteration) {
                placesMap.put(idOfUser,numberOfPlayersWithAssignedPlaces+1);
                numberOfPlayersWithAssignedPlaceInThisIteration++;
                overalScoreMap.remove(idOfUser);
            }
            numberOfPlayersWithAssignedPlaces += numberOfPlayersWithAssignedPlaceInThisIteration;
        }

        return placesMap;
    }


    SingleUserScore getScore(PullUpSeries pullUpSeries){
        SingleUserScore singleUserScore = new SingleUserScore();
        singleUserScore.setScore(Math.toIntExact(pullUpSeries.getScore()));
        singleUserScore.setId(pullUpSeries.getUser().getId());
        return  singleUserScore;
    };


}
