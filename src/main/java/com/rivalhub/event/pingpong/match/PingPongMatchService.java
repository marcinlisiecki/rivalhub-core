package com.rivalhub.event.pingpong.match;

import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PingPongMatchService {
    private final RepositoryManager repositoryManager;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    private final PingPongMatchMapper pingPongMatchMapper;
    private final PingPongMatchHelper pingPongMatchHelper;

    public ViewPingPongMatchDTO createPingPongMatch(Long organizationId, Long eventId, AddPingPongMatchDTO pingPongMatchDTO) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        PingPongMatch pingPongMatch = pingPongMatchMapper.map(pingPongMatchDTO);

        return pingPongMatchHelper.save(organization, requestUser, pingPongEvent, pingPongMatch);
    }

    public boolean setResultApproval(Long organizationId, Long eventId, Long matchId, String email, boolean approve) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);
        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        return pingPongMatchHelper.setResultApproval(loggedUser, pingPongEvent, matchId, approve);
    }

    public ViewPingPongMatchDTO findPingPongMatch(Long organizationId, Long eventId, Long matchId, String email) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);

        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchHelper.findMatchInEvent(pingPongEvent, matchId);

        return pingPongMatchMapper.map(pingPongMatch);

    }


    public List<PingPongSet> addResult(Long organizationId, Long eventId, Long matchId, String email, List<PingPongSet> sets) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData loggedUser = repositoryManager.findUserByEmail(email);
        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        PingPongMatch pingPongMatch = pingPongMatchHelper.findMatchInEvent(pingPongEvent, matchId);

        pingPongMatch.getSets().addAll(sets);
        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);

        return savedMatch.getSets();
    }
}
