package com.rivalhub.event.pingpong.match;

import com.rivalhub.common.exception.*;
import com.rivalhub.event.EventType;
import com.rivalhub.event.match.MatchApprovalService;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.Stats;
import com.rivalhub.organization.StatsRepository;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PingPongMatchService implements MatchService {
    private final OrganizationRepository organizationRepository;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    private final PingPongMatchMapper pingPongMatchMapper;
    private final UserRepository userRepository;
    private final StatsRepository statsRepository;


    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PingPongMatch pingPongMatch = pingPongMatchMapper.map(MatchDTO, organization);

        return save(pingPongEvent, pingPongMatch);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.PING_PONG.name());
    }



    public boolean setResultApproval(Long eventId, Long matchId, Long organizationId) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, pingPongEvent, matchId, organizationId);
    }

    public ViewPingPongMatchDTO findMatch(Long eventId, Long matchId) {
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = findMatchInEvent(pingPongEvent, matchId);

        return pingPongMatchMapper.map(pingPongMatch);
    }

    public List<ViewMatchDto> findMatches(Long eventId) {
        PingPongEvent pingPongEvent = pingPongEventRepository.
                findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        List<PingPongMatch> pingPongMatches = pingPongEvent.getPingPongMatchList();


        return new ArrayList<ViewMatchDto>(pingPongMatches.stream().map(pingPongMatchMapper::map).toList());
    }

    public List<PingPongSet> addResult(Long eventId, Long matchId, List<PingPongSet> sets, Long organizationId) {
        var loggedUser = SecurityUtils.getUserFromSecurityContext();
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = findMatchInEvent(pingPongEvent, matchId);

        setApproveAndNotifications(loggedUser, pingPongMatch, eventId);
        MatchApprovalService.findNotificationToDisActivate(findUserTeam(pingPongMatch, loggedUser), matchId, EventType.PING_PONG, userRepository);

        addPingPongSetsIn(pingPongMatch, sets);
        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);
        return savedMatch.getSets();
    }

    private void addPingPongSetsIn(PingPongMatch pingPongMatch, List<PingPongSet> sets) {
        pingPongMatch.getSets().addAll(sets);
    }

    private MatchDto save(PingPongEvent pingPongEvent, PingPongMatch pingPongMatch) {

        addPingPongMatch(pingPongEvent, pingPongMatch);
        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);
        pingPongEventRepository.save(pingPongEvent);
        return pingPongMatchMapper.mapToMatchDto(savedMatch);
    }

    private void setApproveAndNotifications(UserData loggedUser, PingPongMatch pingPongMatch, Long eventId, Long organizationId) {
        pingPongMatch.getUserApprovalMap().keySet().forEach(key -> pingPongMatch.getUserApprovalMap().put(key,false));
        pingPongMatch.getUserApprovalMap().put(loggedUser.getId(),true);
        if(loggedUser.getNotifications().stream().anyMatch(notification -> notification.getType() == EventType.PING_PONG && notification.getMatchId() == pingPongMatch.getId())) {
            loggedUser.getNotifications().stream().filter(notification -> notification.getType() == EventType.PING_PONG && notification.getMatchId() == pingPongMatch.getId()).findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.CONFIRMED);
            userRepository.save(loggedUser);
        }
        pingPongMatch.getTeam1()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId() && userData.getNotifications().stream().noneMatch(notification -> notification.getType() == EventType.PING_PONG && notification.getMatchId() == pingPongMatch.getId()))
                .forEach(userData -> saveNotification(userData,EventType.PING_PONG, pingPongMatch.getId(), eventId, organizationId));
        pingPongMatch.getTeam2()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId() && userData.getNotifications().stream().noneMatch(notification -> notification.getType() == EventType.PING_PONG && notification.getMatchId() == pingPongMatch.getId()))
                .forEach(userData -> saveNotification(userData,EventType.PING_PONG, pingPongMatch.getId(), eventId, organizationId));
        pingPongMatch.getTeam1()
                .stream()
                .filter(userData -> (userData.getId() != loggedUser.getId()))
                .forEach(userData ->{
                    userData.getNotifications()
                            .stream()
                            .filter(notification -> notification.getType() == EventType.PING_PONG && notification.getMatchId() == pingPongMatch.getId())
                            .findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.NOT_CONFIRMED);
                    userRepository.save(userData);
                });

        pingPongMatch.getTeam2()
                .stream()
                .filter(userData -> (userData.getId() != loggedUser.getId()))
                .forEach(userData ->{
                    userData.getNotifications()
                            .stream()
                            .filter(notification -> notification.getType() == EventType.PING_PONG && notification.getMatchId() == pingPongMatch.getId())
                            .findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.NOT_CONFIRMED);
                    userRepository.save(userData);
                });

    }

    private void addStats(Long organizationId, PingPongMatch pingPongMatch) {
        List<UserData> matchParticipants = Stream.concat(pingPongMatch.getTeam1().stream(), pingPongMatch.getTeam2()
                        .stream())
                .toList();
        List<Stats> statsList = statsRepository.findByUserAndOrganization(organizationId, matchParticipants);


        matchParticipants.forEach(userData -> statsList.forEach(stats -> {
            if (stats.getUserData().equals(userData)) {
                stats.setGamesInPingPong(stats.getGamesInPingPong() + 1);
                if (checkIfUserWon(userData, pingPongMatch)) {
                    stats.setWinInPingPong(stats.getWinInPingPong() + 1);
                }
            }
        }));

        statsRepository.saveAll(statsList);
    }

    private boolean checkIfUserWon(UserData user, PingPongMatch pingPongMatch) {
        int team1WonSets = 0;
        int team2WonSets = 0;

        List<PingPongSet> sets = pingPongMatch.getSets();

        for (PingPongSet set : sets) {
            if (set.getTeam1Score() > set.getTeam2Score()) {
                team1WonSets += 1;
            } else if (set.getTeam1Score() < set.getTeam2Score()) {
                team2WonSets += 1;
            }
        }

        if (team1WonSets > team2WonSets && pingPongMatch.getTeam1().contains(user)) {
            return true;
        }
        if (team2WonSets > team1WonSets && pingPongMatch.getTeam2().contains(user)) {
            return true;
        }

        return false;
    }

    private void saveNotification(UserData userData, EventType type, Long matchId, Long eventId, Long organizationId) {
        MatchApprovalService.saveNotification(userData, type, matchId, eventId, userRepository, organizationId);
    }

    private void addPingPongMatch(PingPongEvent pingPongEvent, PingPongMatch pingPongMatch) {
        pingPongEvent.getPingPongMatchList().add(pingPongMatch);
    }

    private boolean setResultApproval(UserData loggedUser, PingPongEvent pingPongEvent, Long matchId, Long organizationId) {
        PingPongMatch pingPongMatch = pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, pingPongMatch);

        if(pingPongMatchMapper.isApprovedByDemanded(pingPongMatch)){
            addStats(organizationId, pingPongMatch);

                MatchApprovalService.findNotificationToDisActivate(pingPongMatch.getTeam2(), matchId, EventType.PING_PONG, userRepository);
                MatchApprovalService.findNotificationToDisActivate(pingPongMatch.getTeam1(), matchId, EventType.PING_PONG, userRepository);

        } else {
            MatchApprovalService.findNotificationToDisActivate(findUserTeam(pingPongMatch, loggedUser), matchId, EventType.PING_PONG, userRepository);
        }
        pingPongMatchRepository.save(pingPongMatch);
        return pingPongMatch.getUserApprovalMap().get(loggedUser.getId());
    }

    private List<UserData> findUserTeam(PingPongMatch pingPongMatch, UserData loggedUser) {
        if(pingPongMatch.getTeam1().stream().anyMatch(userData -> userData.getId() == loggedUser.getId())){
            return pingPongMatch.getTeam1();
        }
        if(pingPongMatch.getTeam2().stream().anyMatch(userData -> userData.getId() == loggedUser.getId())){
            return pingPongMatch.getTeam2();
        }
        throw new UserNotFoundException();
    }

    private void setApprove(UserData loggedUser, PingPongMatch pingPongMatch) {
        pingPongMatch.getUserApprovalMap().replace(loggedUser.getId(), !(pingPongMatch.getUserApprovalMap().get(loggedUser.getId())));
    }



    private PingPongMatch findMatchInEvent(PingPongEvent pingPongEvent, Long matchId) {
        return pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }

    public PingPongSet editPingPongSet(Long eventId, Long matchId, PingPongSet pingPongSet) {
        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PingPongMatch match = findMatchInEvent(pingPongEvent, matchId);

        PingPongSet setToUpdate = match.getSets().stream()
                .filter(set -> set.getId().equals(pingPongSet.getId()))
                .findFirst()
                .orElseThrow(SetNotFoundException::new);

        match.getSets().remove(setToUpdate);
        match.getSets().add(pingPongSet);

        pingPongMatchRepository.save(match);
        return pingPongSet;
    }

    public void deletePingPongSet(Long eventId, Long matchId, PingPongSet pingPongSet) {
        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PingPongMatch match = findMatchInEvent(pingPongEvent, matchId);

        if (match.getSets().isEmpty()) return;
        if (match.getUserApprovalMap().containsValue(false)) {
            match.getSets().remove(pingPongSet);
        }

        match.getSets()
                .forEach(set -> {
                    if (set.getSetNr() > pingPongSet.getSetNr()) {
                        set.setSetNr(set.getSetNr() - 1);
                    }
                });

        pingPongMatchRepository.save(match);
    }



}
