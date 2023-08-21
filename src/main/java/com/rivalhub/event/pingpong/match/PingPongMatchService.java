package com.rivalhub.event.pingpong.match;

import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.MatchNotFoundException;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PingPongMatchService {
    private final OrganizationRepository organizationRepository;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    private final PingPongMatchMapper pingPongMatchMapper;

    public ViewPingPongMatchDTO createPingPongMatch(Long organizationId, Long eventId, AddPingPongMatchDTO pingPongMatchDTO) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchMapper.map(pingPongMatchDTO, organization);

        return save(requestUser, pingPongEvent, pingPongMatch);
    }

    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, pingPongEvent, matchId, approve);
    }

    public ViewPingPongMatchDTO findPingPongMatch(Long eventId, Long matchId) {
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = findMatchInEvent(pingPongEvent, matchId);

        return pingPongMatchMapper.map(pingPongMatch);
    }

    public List<ViewPingPongMatchDTO> findPingPongMatches(Long eventId) {
        PingPongEvent pingPongEvent = pingPongEventRepository.
                findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        List<PingPongMatch> pingPongMatches = pingPongEvent.getPingPongMatchList();

        return pingPongMatches.stream().map(pingPongMatchMapper::map).toList();
    }

    public List<PingPongSet> addResult(Long eventId, Long matchId, List<PingPongSet> sets) {
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = findMatchInEvent(pingPongEvent, matchId);

        addPingPongSetsIn(pingPongMatch, sets);
        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);

        return savedMatch.getSets();
    }

    private void addPingPongSetsIn(PingPongMatch pingPongMatch, List<PingPongSet> sets) {
        pingPongMatch.getSets().addAll(sets);
    }

    private ViewPingPongMatchDTO save(UserData loggedUser, PingPongEvent pingPongEvent,
                              PingPongMatch pingPongMatch){
        boolean loggedUserInTeam1 = pingPongMatch.getTeam1()
                .stream().anyMatch(loggedUser::equals);

        if (loggedUserInTeam1) pingPongMatch.setTeam1Approval(true);

        boolean loggedUserInTeam2 = pingPongMatch.getTeam2()
                .stream().anyMatch(loggedUser::equals);

        if (loggedUserInTeam2) pingPongMatch.setTeam2Approval(true);

        addPingPongMatch(pingPongEvent, pingPongMatch);

        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);
        pingPongEventRepository.save(pingPongEvent);

        return pingPongMatchMapper.map(savedMatch);
    }

    private void addPingPongMatch(PingPongEvent pingPongEvent,PingPongMatch pingPongMatch){
        pingPongEvent.getPingPongMatchList().add(pingPongMatch);
    }

    private boolean setResultApproval(UserData loggedUser, PingPongEvent pingPongEvent, Long matchId, boolean approve) {
        PingPongMatch pingPongMatch = pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);

        if(pingPongMatch.getTeam1().stream().anyMatch(loggedUser::equals)) pingPongMatch.setTeam1Approval(approve);
        if(pingPongMatch.getTeam2().stream().anyMatch(loggedUser::equals)) pingPongMatch.setTeam2Approval(approve);

        pingPongMatchRepository.save(pingPongMatch);
        return approve;
    }

    private PingPongMatch findMatchInEvent(PingPongEvent pingPongEvent, Long matchId){
        return pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }
}
