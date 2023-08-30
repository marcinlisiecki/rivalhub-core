package com.rivalhub.event.darts.match;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.darts.match.result.DartMatchResultCalculator;
import com.rivalhub.event.darts.match.result.DartRound;
import com.rivalhub.event.darts.match.result.Leg;
import com.rivalhub.event.darts.match.result.SinglePlayerScoreInRound;
import com.rivalhub.event.darts.match.result.variables.DartFormat;
import com.rivalhub.event.darts.match.result.variables.DartMode;
import com.rivalhub.event.match.MatchApprovalService;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.pullups.match.PullUpMatch;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DartMatchMapper {
    private final AutoMapper autoMapper;
    private final DartMatchResultCalculator dartMatchResultCalculator;

    DartMatch map(MatchDto matchDto, Organization organization){

        DartMatch dartMatch = new DartMatch();

        List<UserData> players = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam1Ids()))
                .toList();

        dartMatch.setParticipants(players);
        dartMatch.setDartFormat(DartFormat.valueOf(matchDto.getDartFormat()));
        dartMatch.setDartMode(DartMode.valueOf(matchDto.getDartMode()));
        dartMatch.setUserApprovalMap(MatchApprovalService.prepareApprovalMap(matchDto));
        dartMatch.setEventId(matchDto.getEventId());
        dartMatch.setEventType(matchDto.getEventType());
        return dartMatch;
    }

    public MatchDto mapToMatchDto(DartMatch dartMatch) {
        MatchDto dartMatchDTO = new MatchDto();

        List<UserDetailsDto> participants = dartMatch.getParticipants()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        dartMatchDTO.setId(dartMatch.getId());
        dartMatchDTO.setTeam1Ids(participants.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        dartMatchDTO.setUserApprovalMap(dartMatch.getUserApprovalMap());
        dartMatchDTO.setEventId(dartMatch.getEventId());
        dartMatchDTO.setEventType(dartMatch.getEventType());

        return dartMatchDTO;
    }


    public ViewDartMatchDto map(DartMatch dartMatch){

        ViewDartMatchDto viewDartMatch = new ViewDartMatchDto();
        List<UserDetailsDto> players = dartMatch.getParticipants().stream()
                .map(autoMapper::mapToUserDetails)
                .toList();
        viewDartMatch.setUserDetails(players);
        viewDartMatch.setDateFormat(dartMatch.getDartFormat());
        viewDartMatch.setDartMode(dartMatch.getDartMode());
        viewDartMatch.setEventId(dartMatch.getEventId());
        viewDartMatch.setEventType(dartMatch.getEventType());
        dartMatchResultCalculator.calculateResults(viewDartMatch,dartMatch);
        viewDartMatch.setUserApprovalMap(viewDartMatch.getUserApprovalMap());
        viewDartMatch.setApproved(isApprovedByDemanded(dartMatch));
        viewDartMatch.setId(dartMatch.getId());
        return viewDartMatch;
    }
    private boolean isApprovedByDemanded(DartMatch dartMatch){
        int numberOfUserApproved = 0;
        for (Long userId: dartMatch.getUserApprovalMap().keySet()) {
            if(dartMatch.getUserApprovalMap().get(userId))
                numberOfUserApproved++;
        }
        return numberOfUserApproved>(dartMatch.getParticipants().size()/2);
    }
}
