package com.rivalhub.event.pullups.match;

import com.rivalhub.common.exception.*;
import com.rivalhub.event.EventType;
import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.darts.match.DartMatch;
import com.rivalhub.event.darts.match.result.DartRound;
import com.rivalhub.event.darts.match.result.Leg;
import com.rivalhub.event.darts.match.result.SinglePlayerScoreInRound;
import com.rivalhub.event.match.MatchApprovalService;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;

import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.pullups.PullUpEventRepository;
import com.rivalhub.event.pullups.match.result.*;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.event.tablefootball.match.TableFootballMatch;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PullUpMatchService implements MatchService {

    private final OrganizationRepository organizationRepository;
    private final PullUpEventRepository pullUpEventRepository;
    private final PullUpMatchRepository pullUpMatchRepository;
    private final PullUpMatchMapper pullUpMatchMapper;
    private final PullUpResultMapper pullUpResultMapper;
    private final PullUpSeriesRepository pullUpSeriesRepository;
    private final UserRepository userRepository;
    @Override
    public boolean setResultApproval(Long eventId, Long matchId) {
            var requestUser = SecurityUtils.getUserFromSecurityContext();
            PullUpEvent pullUpEvent = pullUpEventRepository
                    .findById(eventId)
                    .orElseThrow(EventNotFoundException::new);

            return setResultApproval(requestUser, pullUpEvent, matchId);
    }

    private boolean setResultApproval(UserData loggedUser, PullUpEvent pullUpEvent, Long matchId) {
        PullUpMatch pullUpMatch = pullUpEvent.getPullUpMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, pullUpMatch);
        if(pullUpMatchMapper.isApprovedByDemanded(pullUpMatch)){
            MatchApprovalService.findNotificationToDisActivate(pullUpMatch.getParticipants(), matchId, EventType.PULL_UPS, userRepository);
        }else {
            MatchApprovalService.findNotificationToDisActivate(List.of(loggedUser), matchId, EventType.PULL_UPS, userRepository);
        }
        pullUpMatchRepository.save(pullUpMatch);
        return pullUpMatch.getUserApprovalMap().get(loggedUser.getId());
    }

    private void setApprove(UserData loggedUser, PullUpMatch pullUpMatch) {
        pullUpMatch.getUserApprovalMap().replace(loggedUser.getId(), !(pullUpMatch.getUserApprovalMap().get(loggedUser.getId())));
    }

    private void setApproveAndNotifications(UserData loggedUser, PullUpMatch pullUpMatch, Long eventId) {
        pullUpMatch.getUserApprovalMap().keySet().forEach(key -> pullUpMatch.getUserApprovalMap().put(key,false));
        pullUpMatch.getUserApprovalMap().put(loggedUser.getId(),true);
        if(loggedUser.getNotifications().stream().anyMatch(notification -> notification.getType() == EventType.PULL_UPS && notification.getMatchId() == pullUpMatch.getId())) {
            loggedUser.getNotifications().stream().filter(notification -> notification.getType() == EventType.PULL_UPS && notification.getMatchId() == pullUpMatch.getId()).findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.CONFIRMED);
            userRepository.save(loggedUser);
        }
        pullUpMatch.getParticipants()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId() && userData.getNotifications().stream()
                        .noneMatch(notification -> notification.getType() == EventType.PULL_UPS && notification.getMatchId() == pullUpMatch.getId()))
                .forEach(userData -> saveNotification(userData,EventType.PULL_UPS, pullUpMatch.getId(), eventId));
       pullUpMatch.getParticipants()
                .stream()
                .filter(userData -> (userData.getId() != loggedUser.getId()))
                .forEach(userData ->{
                    userData.getNotifications()
                            .stream()
                            .filter(notification -> notification.getType() == EventType.PULL_UPS && notification.getMatchId() == pullUpMatch.getId())
                            .findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.NOT_CONFIRMED);
                    userRepository.save(userData);
                });
    }



    private void saveNotification(UserData userData, EventType type, Long matchId, Long eventId) {
        MatchApprovalService.saveNotification(userData, type, matchId, eventId, userRepository);
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        PullUpEvent pullUpEvent = pullUpEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PullUpMatch pullUpMatch = pullUpMatchMapper.map(MatchDTO, organization);

        return save( pullUpEvent, pullUpMatch);

    }

    private MatchDto save(PullUpEvent pullUpEvent,
                          PullUpMatch pullUpMatch){

        addPullUpMatch(pullUpEvent, pullUpMatch);

        PullUpMatch savedMatch = pullUpMatchRepository.save(pullUpMatch);
        pullUpEventRepository.save(pullUpEvent);

        return pullUpMatchMapper.mapToMatchDto(savedMatch);
    }

    private void addPullUpMatch(PullUpEvent pullUpEvent, PullUpMatch pullUpMatch) {
        pullUpEvent.getPullUpMatchList().add(pullUpMatch);
    }

    @Override
    public ViewMatchDto findMatch(Long eventId, Long matchId) {
        PullUpEvent pullUpEvent = pullUpEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PullUpMatch pullUpMatch = findMatchInEvent(pullUpEvent, matchId);

        return pullUpMatchMapper.map(pullUpMatch);
    }

    private PullUpMatch findMatchInEvent(PullUpEvent pullUpEvent, Long matchId) {
        return pullUpEvent.getPullUpMatchList().stream()
                .filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }

    @Override
    public List<ViewMatchDto> findMatches(Long eventId) {
        PullUpEvent pullUpEvent = pullUpEventRepository.
                findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        List<PullUpMatch> pullUpMatches = pullUpEvent.getPullUpMatchList();

        return new ArrayList<ViewMatchDto>(pullUpMatches.stream().map(pullUpMatchMapper::map).toList());
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.PULL_UPS.name());
    }

    public ViewMatchDto addResult(Long eventId, Long matchId, List<PullUpSeriesAddDto> pullUpSeriesAddDto) {
        var loggedUser = SecurityUtils.getUserFromSecurityContext();
        PullUpEvent pullUpEvent = pullUpEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PullUpMatch pullUpMatch = findMatchInEvent(pullUpEvent, matchId);
        List<PullUpSeries> pullUpSeries = new ArrayList<>();
        pullUpSeriesAddDto.stream().forEach(pullUpSeriesDto -> pullUpSeries.add(pullUpResultMapper.map(pullUpSeriesDto,pullUpMatch)));
        pullUpSeries.stream().forEach(pullUpSerie -> pullUpSeriesRepository.save(pullUpSerie));
        pullUpMatch.setPullUpSeries(pullUpSeries);
        setApproveAndNotifications(loggedUser, pullUpMatch,eventId);
        PullUpMatch savedMatch = pullUpMatchRepository.save(pullUpMatch);

        return pullUpMatchMapper.map(savedMatch);
    }

    public void deletePullUpSeries(Long eventId, Long matchId, Long seriesId) {
        PullUpEvent pullUpEvent = pullUpEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PullUpMatch match = findMatchInEvent(pullUpEvent, matchId);

        if (match.getPullUpSeries().isEmpty()) return;
        if (match.getUserApprovalMap().containsValue(false)) {
            List<PullUpSeries> seriesToDelete = match.getPullUpSeries().stream().filter(s -> s.getSeriesID().equals(seriesId)).toList();
            match.getPullUpSeries().removeAll(seriesToDelete);
        }

        match.getPullUpSeries()
                .forEach(set -> {
                    if (set.getSeriesID() > seriesId) {
                        set.setSeriesID(set.getSeriesID() - 1);
                    }
                });

        pullUpMatchRepository.save(match);
    }



}