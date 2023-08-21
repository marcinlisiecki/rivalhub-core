package com.rivalhub.event.tablefootball.match;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.event.pingpong.match.ViewPingPongMatchDTO;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        tableFootballMatchDto.setTeam1Approval(tableFootballMatch.isTeam1Approval());
        tableFootballMatchDto.setTeam2Approval(tableFootballMatch.isTeam2Approval());

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
        viewTableFootballMatchDTO.setTeam1Approval(tableFootballMatch.isTeam1Approval());
        viewTableFootballMatchDTO.setTeam2Approval(tableFootballMatch.isTeam2Approval());

        return viewTableFootballMatchDTO;
    }
}
