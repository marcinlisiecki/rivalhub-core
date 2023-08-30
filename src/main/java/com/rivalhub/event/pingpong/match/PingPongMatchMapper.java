package com.rivalhub.event.pingpong.match;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.match.MatchApprovalService;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        pingPongMatch.setUserApprovalMap(MatchApprovalService.prepareApprovalMap(matchDto));
        pingPongMatch.setEventId(matchDto.getEventId());
        pingPongMatch.setEventType(matchDto.getEventType());
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
        pingPongMatchDTO.setUserApprovalMap(pingPongMatch.getUserApprovalMap());
        pingPongMatchDTO.setEventId(pingPongMatch.getEventId());
        pingPongMatchDTO.setEventType(pingPongMatch.getEventType());

        return pingPongMatchDTO;
    }



    public ViewPingPongMatchDTO map(PingPongMatch pingPongMatch){
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
        pingPongMatchDTO.setUserApprovalMap(pingPongMatch.getUserApprovalMap());
        pingPongMatchDTO.setApproved(isApprovedByDemanded(pingPongMatch));
        pingPongMatchDTO.setEventId(pingPongMatch.getEventId());
        pingPongMatchDTO.setEventType(pingPongMatch.getEventType());
        return pingPongMatchDTO;
    }

    boolean isApprovedByDemanded(PingPongMatch pingPongMatch){
        List<Long> userApproved = new ArrayList<>();
        for (Long userId: pingPongMatch.getUserApprovalMap().keySet()) {
            if(pingPongMatch.getUserApprovalMap().get(userId))
                userApproved.add(userId);
        }
        boolean teamOneApproved = false;
        for (UserData userData : pingPongMatch.getTeam1()) {
            for(Long userApprove : userApproved){
                if(userData.getId() == userApprove){
                    teamOneApproved = true;
                }
            }
        };
        boolean teamTwoApproved = false;
        for (UserData userData : pingPongMatch.getTeam2()) {
            for(Long userApprove : userApproved){
                if(userData.getId() == userApprove){
                    teamTwoApproved = true;
                }
            }
        };
        return teamTwoApproved&&teamOneApproved;
    }

}
