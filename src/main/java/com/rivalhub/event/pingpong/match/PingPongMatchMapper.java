package com.rivalhub.event.pingpong.match;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PingPongMatchMapper {
    private final AutoMapper autoMapper;

    PingPongMatch map(MatchDto matchDto, Organization organization){
        PingPongMatch pingPongMatch = new PingPongMatch();

        List<UserData> team1 = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam1Ids()))
                .toList();

        List<UserData> team2 = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam2Ids()))
                .toList();

        pingPongMatch.setTeam1(team1);
        pingPongMatch.setTeam2(team2);

        return pingPongMatch;
    }

    MatchDto mapToMatchDto(PingPongMatch pingPongMatch){
        MatchDto pingPongMatchDTO = new MatchDto();

        List<UserDetailsDto> team1 = pingPongMatch.getTeam1()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();
        List<UserDetailsDto> team2 = pingPongMatch.getTeam2()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        pingPongMatchDTO.setId(pingPongMatch.getId());
        pingPongMatchDTO.setTeam1Ids(team1.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        pingPongMatchDTO.setTeam2Ids(team2.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        pingPongMatchDTO.setTeam1Approval(pingPongMatch.isTeam1Approval());
        pingPongMatchDTO.setTeam2Approval(pingPongMatch.isTeam2Approval());

        return pingPongMatchDTO;
    }



    ViewPingPongMatchDTO map(PingPongMatch pingPongMatch){
        ViewPingPongMatchDTO pingPongMatchDTO = new ViewPingPongMatchDTO();

        List<UserDetailsDto> team1 = pingPongMatch.getTeam1()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();
        List<UserDetailsDto> team2 = pingPongMatch.getTeam2()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        pingPongMatchDTO.setId(pingPongMatch.getId());
        pingPongMatchDTO.setTeam1(team1);
        pingPongMatchDTO.setTeam2(team2);
        pingPongMatchDTO.setSets(pingPongMatch.getSets());
        pingPongMatchDTO.setTeam1Approval(pingPongMatch.isTeam1Approval());
        pingPongMatchDTO.setTeam2Approval(pingPongMatch.isTeam2Approval());

        return pingPongMatchDTO;
    }

}
