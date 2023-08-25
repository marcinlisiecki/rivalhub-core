package com.rivalhub.event.billiards.match;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.organization.Organization;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BilliardsMatchMapper {
    private final AutoMapper autoMapper;
    BilliardsMatch map(MatchDto matchDto, Organization organization){
        BilliardsMatch billiardsMatch = new BilliardsMatch();

        List<UserData> team1 = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam1Ids()))
                .toList();

        List<UserData> team2 = organization.getUserList().stream()
                .filter(EventUtils.getUserFromOrganization(matchDto.getTeam2Ids()))
                .toList();

        billiardsMatch.setTeam1(team1);
        billiardsMatch.setTeam2(team2);

        return billiardsMatch;
    }
    MatchDto mapToMatchDto(BilliardsMatch billiardsMatch){
        MatchDto matchDto = new MatchDto();
        List<UserDetailsDto> team1 = billiardsMatch.getTeam1()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();
        List<UserDetailsDto> team2 = billiardsMatch.getTeam2()
                .stream().map(autoMapper::mapToUserDetails)
                .toList();

        matchDto.setId(billiardsMatch.getId());
        matchDto.setTeam1Ids(team1.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        matchDto.setTeam2Ids(team2.stream().map(UserDetailsDto::getId).collect(Collectors.toList()));
        matchDto.setTeam1Approval(billiardsMatch.isTeam1Approval());
        matchDto.setTeam2Approval(billiardsMatch.isTeam2Approval());
        return matchDto;
    };
    ViewMatchDto map(BilliardsMatch billiardsMatch){
        ViewBilliardMatchDTO viewBilliardMatchDTO = new ViewBilliardMatchDTO();
        viewBilliardMatchDTO.setWinType(billiardsMatch.getWinType());
        viewBilliardMatchDTO.setHowManyBillsLeftTeam2(billiardsMatch.getHowManyBillsLeftTeam2());
        viewBilliardMatchDTO.setTeam1PlaysFull(billiardsMatch.isTeam1PlaysFull());
        viewBilliardMatchDTO.setHowManyBillsLeftTeam1(billiardsMatch.getHowManyBillsLeftTeam1());
        viewBilliardMatchDTO.setTeam1HadPottedFirst(billiardsMatch.isTeam1HadPottedFirst());
        viewBilliardMatchDTO.setTeam2(billiardsMatch.getTeam2().stream().map(autoMapper::mapToUserDetails).toList());
        viewBilliardMatchDTO.setTeam1(billiardsMatch.getTeam1().stream().map(autoMapper::mapToUserDetails).toList());
        viewBilliardMatchDTO.setTeam1Won(billiardsMatch.isTeam1Won());
        viewBilliardMatchDTO.setTeam2Won(billiardsMatch.isTeam2Won());
        return viewBilliardMatchDTO;
    }

}
