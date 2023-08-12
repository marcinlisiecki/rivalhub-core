package com.rivalhub.event.pingpong;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.MatchNotFoundException;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PingPongMatchService {
    private final RepositoryManager repositoryManager;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchMapper pingPongMatchMapper;
    private final PingPongMatchRepository pingPongMatchRepository;

    PingPongMatch createPingPongMatch(Long organizationId, Long eventId, String email, PingPongMatchDTO pingPongMatchDTO) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);

        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchMapper.map(pingPongMatchDTO);

        boolean present = pingPongMatch.getTeam1()
                .stream().anyMatch(loggedUser::equals);
        if (present) pingPongMatch.setTeam1Approval(true);
        present = pingPongMatch.getTeam2()
                .stream().anyMatch(loggedUser::equals);
        if (present) pingPongMatch.setTeam2Approval(true);

        pingPongEvent.addPingPongMatch(pingPongMatch);
        pingPongEventRepository.save(pingPongEvent);


        return pingPongMatch;
    }

    boolean setResultApproval(Long organizationId, Long eventId, Long matchId, String email, boolean approve) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);

        if(pingPongMatch.getTeam1().stream().anyMatch(loggedUser::equals)) pingPongMatch.setTeam1Approval(approve);
        if(pingPongMatch.getTeam2().stream().anyMatch(loggedUser::equals)) pingPongMatch.setTeam2Approval(approve);

        pingPongMatchRepository.save(pingPongMatch);
        return approve;
    }

    PingPongMatchDTO findPingPongMatch(Long organizationId, Long eventId, Long matchId, String email) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);

        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);

        return pingPongMatchMapper.map(pingPongMatch);

    }
}
