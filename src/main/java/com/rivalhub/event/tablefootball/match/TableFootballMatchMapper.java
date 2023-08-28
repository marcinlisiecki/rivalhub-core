package com.rivalhub.event.tablefootball.match;

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

@Component
@RequiredArgsConstructor
public class TableFootballMatchMapper {
    private final AutoMapper autoMapper;

    TableFootballMatch map(MatchDto matchDto, Organization organization){
        TableFootballMatch tableFootballMatch = new TableFootballMatch();

        List<UserData> team1 = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam1Ids()))
                .toList();

        List<UserData> team2 = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam2Ids()))
                .toList();

        tableFootballMatch.setTeam1(team1);
        tableFootballMatch.setTeam2(team2);
        tableFootballMatch.setUserApprovalMap(MatchApprovalService.prepareApprovalMap(matchDto));
        return tableFootballMatch;
    }

    MatchDto mapToMatchDto(TableFootballMatch tableFootballMatch){
        MatchDto tableFootballMatchDto = new MatchDto();

        List<UserDetailsDto> team1 = tableFootballMatch.getTeam1()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();
        List<UserDetailsDto> team2 = tableFootballMatch.getTeam2()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        tableFootballMatchDto.setId(tableFootballMatch.getId());
        tableFootballMatchDto.setTeam1Ids(team1.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        tableFootballMatchDto.setTeam2Ids(team2.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        tableFootballMatchDto.setUserApprovalMap(tableFootballMatch.getUserApprovalMap());

        return tableFootballMatchDto;
    }

    ViewTableFootballMatchDTO map(TableFootballMatch tableFootballMatch){
        ViewTableFootballMatchDTO viewTableFootballMatchDTO = new ViewTableFootballMatchDTO();

        List<UserDetailsDto> team1 = tableFootballMatch.getTeam1()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();
        List<UserDetailsDto> team2 = tableFootballMatch.getTeam2()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        viewTableFootballMatchDTO.setId(tableFootballMatch.getId());
        viewTableFootballMatchDTO.setTeam1(team1);
        viewTableFootballMatchDTO.setTeam2(team2);
        viewTableFootballMatchDTO.setSets(tableFootballMatch.getSets());
        viewTableFootballMatchDTO.setUserApprovalMap(tableFootballMatch.getUserApprovalMap());
        viewTableFootballMatchDTO.setApproved(isApprovedByDemanded(tableFootballMatch));
        return viewTableFootballMatchDTO;
    }

    boolean isApprovedByDemanded(TableFootballMatch tableFootballMatch){
        List<Long> userApproved = new ArrayList<>();
        for (Long userId: tableFootballMatch.getUserApprovalMap().keySet()) {
            if(tableFootballMatch.getUserApprovalMap().get(userId))
                userApproved.add(userId);
        }
        boolean teamOneApproved = false;
        for (UserData userData : tableFootballMatch.getTeam1()) {
            for(Long userApprove : userApproved){
                if(userData.getId() == userApprove){
                    teamOneApproved = true;
                }
            }
        };
        boolean teamTwoApproved = false;
        for (UserData userData : tableFootballMatch.getTeam2()) {
            for(Long userApprove : userApproved){
                if(userData.getId() == userApprove){
                    teamTwoApproved = true;
                }
            }
        };
        return teamTwoApproved&&teamOneApproved;
    }

}
