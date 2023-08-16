package com.rivalhub.event.pingpong.match;

import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PingPongMatchService {
    private final OrganizationRepository organizationRepository;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    private final PingPongMatchMapper pingPongMatchMapper;
    private final PingPongMatchHelper pingPongMatchHelper;

    public ViewPingPongMatchDTO createPingPongMatch(Long organizationId, Long eventId, AddPingPongMatchDTO pingPongMatchDTO) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchMapper.map(pingPongMatchDTO, organization);

        return pingPongMatchHelper.save(requestUser, pingPongEvent, pingPongMatch);
    }

    public boolean setResultApproval(Long organizationId, Long eventId, Long matchId, boolean approve) {
//        Organization organization = repositoryManager.findOrganizationById(organizationId);
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        return pingPongMatchHelper.setResultApproval(requestUser, pingPongEvent, matchId, approve);
    }

    public ViewPingPongMatchDTO findPingPongMatch(Long organizationId, Long eventId, Long matchId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OrganizationSettingsValidator.userIsInOrganization(organization, requestUser);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchHelper.findMatchInEvent(pingPongEvent, matchId);

        return pingPongMatchMapper.map(pingPongMatch);

    }

    public List<ViewPingPongMatchDTO> findPingPongMatches(Long organizationId, Long eventId) {
        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        List<PingPongMatch> pingPongMatches = pingPongEvent.getPingPongMatchList();

        return pingPongMatches.stream().map(pingPongMatchMapper::map).toList();
    }

    public List<PingPongSet> addResult(Long organizationId, Long eventId, Long matchId, List<PingPongSet> sets) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Organization organization = repositoryManager.findOrganizationById(organizationId);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        PingPongMatch pingPongMatch = pingPongMatchHelper.findMatchInEvent(pingPongEvent, matchId);

        addPingPongSetsIn(pingPongMatch, sets);
        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);

        return savedMatch.getSets();
    }

    private void addPingPongSetsIn(PingPongMatch pingPongMatch, List<PingPongSet> sets) {
        pingPongMatch.getSets().addAll(sets);
    }
}
