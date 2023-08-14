package com.rivalhub.event.pingpong.match;

import com.rivalhub.event.MatchNotFoundException;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PingPongMatchHelper {
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    private final PingPongMatchMapper pingPongMatchMapper;
    ViewPingPongMatchDTO save(Organization organization, UserData loggedUser, PingPongEvent pingPongEvent,
                              PingPongMatch pingPongMatch){
        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        boolean present = pingPongMatch.getTeam1()
                .stream().anyMatch(loggedUser::equals);

        if (present) pingPongMatch.setTeam1Approval(true);

        present = pingPongMatch.getTeam2()
                .stream().anyMatch(loggedUser::equals);

        if (present) pingPongMatch.setTeam2Approval(true);

        addPingPongMatch(pingPongEvent, pingPongMatch);

        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);

        pingPongEventRepository.save(pingPongEvent);

        return pingPongMatchMapper.map(savedMatch);
    }

    private void addPingPongMatch(PingPongEvent pingPongEvent,PingPongMatch pingPongMatch){
        pingPongEvent.getPingPongMatchList().add(pingPongMatch);
    }

    boolean setResultApproval(UserData loggedUser, PingPongEvent pingPongEvent, Long matchId, boolean approve) {
        PingPongMatch pingPongMatch = pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);

        if(pingPongMatch.getTeam1().stream().anyMatch(loggedUser::equals)) pingPongMatch.setTeam1Approval(approve);
        if(pingPongMatch.getTeam2().stream().anyMatch(loggedUser::equals)) pingPongMatch.setTeam2Approval(approve);

        pingPongMatchRepository.save(pingPongMatch);
        return approve;
    }

    PingPongMatch findMatchInEvent(PingPongEvent pingPongEvent, Long matchId){
        return pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }
}
