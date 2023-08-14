package com.rivalhub.event.pingpong.match;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PingPongMatchMapper {
    private final AutoMapper autoMapper;
    private final RepositoryManager repositoryManager;

    PingPongMatch map(AddPingPongMatchDTO pingPongMatchDTO){
        PingPongMatch pingPongMatch = new PingPongMatch();


        List<UserData> team1 = pingPongMatchDTO.getTeam1Ids().stream()
                .map(repositoryManager::findUserById).toList();

        List<UserData> team2 = pingPongMatchDTO.getTeam2Ids().stream()
                .map(repositoryManager::findUserById).toList();

        pingPongMatch.setTeam1(team1);
        pingPongMatch.setTeam2(team2);
        pingPongMatch.setTeam1Score(pingPongMatchDTO.getTeam1Score());
        pingPongMatch.setTeam2Score(pingPongMatchDTO.getTeam2Score());

        return pingPongMatch;
    }

    ViewPingPongMatchDTO map(PingPongMatch pingPongMatch){
        ViewPingPongMatchDTO pingPongMatchDTO = new ViewPingPongMatchDTO();

        List<UserDetailsDto> team1 = pingPongMatch.getTeam1().stream().map(autoMapper::mapToUserDetails).toList();
        List<UserDetailsDto> team2 = pingPongMatch.getTeam2().stream().map(autoMapper::mapToUserDetails).toList();

        pingPongMatchDTO.setId(pingPongMatch.getId());
        pingPongMatchDTO.setTeam1(team1);
        pingPongMatchDTO.setTeam2(team2);
        pingPongMatchDTO.setTeam1Score(pingPongMatch.getTeam1Score());
        pingPongMatchDTO.setTeam2Score(pingPongMatch.getTeam2Score());
        pingPongMatchDTO.setTeam1Approval(pingPongMatch.isTeam1Approval());
        pingPongMatchDTO.setTeam2Approval(pingPongMatch.isTeam2Approval());

        return pingPongMatchDTO;
    }

}
