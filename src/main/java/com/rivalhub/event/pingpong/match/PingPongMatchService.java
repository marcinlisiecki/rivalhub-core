package com.rivalhub.event.pingpong.match;

import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.common.exception.UserNotFoundException;
import com.rivalhub.event.EventType;
import com.rivalhub.common.exception.MatchNotFoundException;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PingPongMatchService implements MatchService {
    private final OrganizationRepository organizationRepository;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    private final PingPongMatchMapper pingPongMatchMapper;
    private final UserRepository userRepository;


    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        PingPongEvent pingPongEvent = pingPongEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PingPongMatch pingPongMatch = pingPongMatchMapper.map(MatchDTO, organization);

        return save(requestUser, pingPongEvent, pingPongMatch);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.PING_PONG.name());
    }


    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        PingPongEvent pingPongEvent = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, pingPongEvent, matchId, approve);
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

    private MatchDto save(UserData loggedUser, PingPongEvent pingPongEvent,
                          PingPongMatch pingPongMatch) {

        addPingPongMatch(pingPongEvent, pingPongMatch);
        PingPongMatch savedMatch = pingPongMatchRepository.save(pingPongMatch);

        setApproveAndNotifications(loggedUser, savedMatch, pingPongEvent.getEventId());

        pingPongEventRepository.save(pingPongEvent);

        return pingPongMatchMapper.mapToMatchDto(savedMatch);
    }

    //todo dac do innej klasy gdzie bedzie korzystac w innych eventach
    private void setApproveAndNotifications(UserData loggedUser, PingPongMatch pingPongMatch, Long eventId) {
        boolean loggedUserInTeam1 = pingPongMatch.getTeam1()
                .stream().anyMatch(loggedUser::equals);

        if (loggedUserInTeam1) {
            pingPongMatch.setTeam1Approval(true);
        } else {
            saveNotification(pingPongMatch.getTeam1(), EventType.PING_PONG, pingPongMatch.getId(), eventId);
        }

        boolean loggedUserInTeam2 = pingPongMatch.getTeam2()
                .stream().anyMatch(loggedUser::equals);

        if (loggedUserInTeam2) {
            pingPongMatch.setTeam2Approval(true);
        } else {
            saveNotification(pingPongMatch.getTeam2(), EventType.PING_PONG, pingPongMatch.getId(), eventId);
        }
    }

    private void saveNotification(List<UserData> team, EventType type, Long matchId, Long eventId) {
        team.forEach(
                userData -> {
                    userData.getNotifications().add(
                            new Notification(eventId, matchId, type, Notification.Status.NOT_CONFIRMED));
                    userRepository.save(userData);
                });
    }

    private void addPingPongMatch(PingPongEvent pingPongEvent, PingPongMatch pingPongMatch) {
        pingPongEvent.getPingPongMatchList().add(pingPongMatch);
    }

    private boolean setResultApproval(UserData loggedUser, PingPongEvent pingPongEvent, Long matchId, boolean approve) {
        PingPongMatch pingPongMatch = pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, approve, pingPongMatch);

        pingPongMatchRepository.save(pingPongMatch);
        return approve;
    }

    //todo ctrl+v do innych eventów
    private void setApprove(UserData loggedUser, boolean approve, PingPongMatch pingPongMatch) {
        if (pingPongMatch.getTeam1().stream().anyMatch(loggedUser::equals)) {
            pingPongMatch.setTeam1Approval(approve);
            findNotificationToDisActivate(pingPongMatch.getTeam1(), pingPongMatch.getId());
        }

        if (pingPongMatch.getTeam2().stream().anyMatch(loggedUser::equals)) {
            pingPongMatch.setTeam2Approval(approve);
            findNotificationToDisActivate(pingPongMatch.getTeam1(), pingPongMatch.getId());
        }
    }

    private void findNotificationToDisActivate(List<UserData> team, Long matchId) {
        team.forEach(
                userData -> {
                    userData.getNotifications()
                            .stream().filter(
                                    notification -> notification.getMatchId().equals(matchId))
                            .findFirst()
                            .orElseThrow(UserNotFoundException::new)
                            .setStatus(Notification.Status.CONFIRMED);
                }
        );
    }

    private PingPongMatch findMatchInEvent(PingPongEvent pingPongEvent, Long matchId) {
        return pingPongEvent.getPingPongMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }
}
