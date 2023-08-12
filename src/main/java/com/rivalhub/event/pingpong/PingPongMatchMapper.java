package com.rivalhub.event.pingpong;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PingPongMatchMapper {
    private final AutoMapper autoMapper;
    private final RepositoryManager repositoryManager;

    public PingPongMatch map(PingPongMatchDTO pingPongMatchDTO){
        PingPongMatch pingPongMatch = new PingPongMatch();

        List<UserData> team1 = pingPongMatchDTO.getTeam1().stream().map(userDetailsDto -> repositoryManager.findUserByEmail(userDetailsDto.getEmail())).toList();
        List<UserData> team2 = pingPongMatchDTO.getTeam2().stream().map(userDetailsDto -> repositoryManager.findUserByEmail(userDetailsDto.getEmail())).toList();


        pingPongMatch.setTeam1(team1);
        pingPongMatch.setTeam2(team2);
        pingPongMatch.setTeam1Score(pingPongMatchDTO.getTeam1Score());
        pingPongMatch.setTeam2Score(pingPongMatchDTO.getTeam2Score());

        return pingPongMatch;
    }
}
