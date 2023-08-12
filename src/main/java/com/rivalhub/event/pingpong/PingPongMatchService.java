package com.rivalhub.event.pingpong;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventNotFoundException;
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
    private final AutoMapper autoMapper;

    PingPongMatch createPingPongMatch(Long organizationId, Long eventId, String email, PingPongMatchDTO pingPongMatchDTO) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);

        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchMapper.map(pingPongMatchDTO);


//        pingPongMatch.getTeam2().stream().map(loggedUser::equals)

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
}
