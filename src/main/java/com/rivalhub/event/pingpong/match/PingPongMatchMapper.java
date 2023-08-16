package com.rivalhub.event.pingpong.match;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Component
public class PingPongMatchMapper {
    private final AutoMapper autoMapper;

    PingPongMatch map(AddPingPongMatchDTO pingPongMatchDTO, Organization organization){
        PingPongMatch pingPongMatch = new PingPongMatch();

        List<UserData> team1 = organization.getUserList().stream()
                .filter(getUserFromOrganization(pingPongMatchDTO.getTeam1Ids()))
                .toList();

        List<UserData> team2 = organization.getUserList().stream()
                .filter(getUserFromOrganization(pingPongMatchDTO.getTeam2Ids()))
                .toList();

        pingPongMatch.setTeam1(team1);
        pingPongMatch.setTeam2(team2);

        return pingPongMatch;
    }

    private Predicate<UserData> getUserFromOrganization(List<Long> pingPongMatchDTO) {
        return userData -> pingPongMatchDTO.contains(userData.getId());
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
