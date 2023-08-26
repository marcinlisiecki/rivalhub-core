package com.rivalhub.event.darts.match;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.darts.match.result.DartMatchResultCalculator;
import com.rivalhub.event.darts.match.result.DartRound;
import com.rivalhub.event.darts.match.result.Leg;
import com.rivalhub.event.darts.match.result.SinglePlayerScoreInRound;
import com.rivalhub.event.darts.match.result.variables.DartFormat;
import com.rivalhub.event.darts.match.result.variables.DartMode;
import com.rivalhub.event.match.MatchDto;
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
        return dartMatch;


    }

    public MatchDto mapToMatchDto(DartMatch dartMatch) {
        MatchDto dartMatchDTO = new MatchDto();

        List<UserDetailsDto> participants = dartMatch.getParticipants()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        dartMatchDTO.setId(dartMatch.getId());
        dartMatchDTO.setTeam1Ids(participants.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
//        dartMatchDTO.setTeam1Approval(dartMatch.isApprovalFirstPlace());
//        dartMatchDTO.setTeam2Approval(dartMatch.isApprovalSecondPlace());
//        dartMatchDTO.setTeam3Approval(dartMatch.isApprovalThirdPlace());

        return dartMatchDTO;
    }


    ViewDartMatchDto map(DartMatch dartMatch){

        ViewDartMatchDto viewDartMatch = new ViewDartMatchDto();
        List<UserDetailsDto> players = dartMatch.getParticipants().stream()
                .map(autoMapper::mapToUserDetails)
                .toList();
        viewDartMatch.setUserDetails(players);
        viewDartMatch.setDateFormat(dartMatch.getDartFormat());
        viewDartMatch.setDartMode(dartMatch.getDartMode());
        dartMatchResultCalculator.calculateResults(viewDartMatch,dartMatch);
        return viewDartMatch;
    }
}
