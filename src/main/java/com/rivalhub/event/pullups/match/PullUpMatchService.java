package com.rivalhub.event.pullups.match;

import com.rivalhub.common.exception.*;
import com.rivalhub.event.EventType;
import com.rivalhub.event.match.MatchApprovalService;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;

import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.pullups.PullUpEventRepository;
import com.rivalhub.event.pullups.match.result.*;
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
import java.util.Map;

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
    private final StatsRepository statsRepository;
    @Override
    public boolean setResultApproval(Long eventId, Long matchId, Long organizationId) {
            var requestUser = SecurityUtils.getUserFromSecurityContext();
            PullUpEvent pullUpEvent = pullUpEventRepository
                    .findById(eventId)
                    .orElseThrow(EventNotFoundException::new);

            return setResultApproval(requestUser, pullUpEvent, matchId, organizationId);
    }

    private boolean setResultApproval(UserData loggedUser, PullUpEvent pullUpEvent, Long matchId, Long organizationId) {
        PullUpMatch pullUpMatch = pullUpEvent.getPullUpMatchList()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, pullUpMatch);
        if(pullUpMatchMapper.isApprovedByDemanded(pullUpMatch)){
            MatchApprovalService.findNotificationToDisActivate(pullUpMatch.getParticipants(), matchId, EventType.PULL_UPS, userRepository);
            addStats(organizationId, pullUpMatch);
        }else {
            MatchApprovalService.findNotificationToDisActivate(List.of(loggedUser), matchId, EventType.PULL_UPS, userRepository);
        }
        pullUpMatchRepository.save(pullUpMatch);
        return pullUpMatch.getUserApprovalMap().get(loggedUser.getId());
    }

    private void setApprove(UserData loggedUser, PullUpMatch pullUpMatch) {
        pullUpMatch.getUserApprovalMap().replace(loggedUser.getId(), !(pullUpMatch.getUserApprovalMap().get(loggedUser.getId())));
    }

    private void setApproveAndNotifications(UserData loggedUser, PullUpMatch pullUpMatch, Long eventId, Long organizationId) {
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
                .forEach(userData -> saveNotification(userData,EventType.PULL_UPS, pullUpMatch.getId(), eventId, organizationId));
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

    private void saveNotification(UserData userData, EventType type, Long matchId, Long eventId, Long organizationId) {
        MatchApprovalService.saveNotification(userData, type, matchId, eventId, userRepository, organizationId);
    }

    private void addStats(Long organizationId, PullUpMatch pullUpMatch) {
        List<UserData> matchParticipants = pullUpMatch.getParticipants();
        List<Stats> statsList = statsRepository.findByUserAndOrganization(organizationId, matchParticipants);


        matchParticipants.forEach(userData -> statsList.forEach(stats -> {
            if (stats.getUserData().equals(userData)) {
                stats.setGamesInPullUps(stats.getGamesInPullUps() + 1);
                if (checkIfUserWon(userData, pullUpMatch)) {
                    stats.setWinInPullUps(stats.getWinInPullUps() + 1);
                }
            }
        }));

        statsRepository.saveAll(statsList);
    }

    private boolean checkIfUserWon(UserData user, PullUpMatch pullUpMatch) {
        Map<Long, Integer> places = pullUpMatchMapper.getPlaces(pullUpMatch);
        Integer userPlace = places.get(user.getId());

        return userPlace == 1;
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

    public ViewMatchDto addResult(Long eventId, Long matchId, List<PullUpSeriesAddDto> pullUpSeriesAddDto, Long organizationId) {
        var loggedUser = SecurityUtils.getUserFromSecurityContext();
        PullUpEvent pullUpEvent = pullUpEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PullUpMatch pullUpMatch = findMatchInEvent(pullUpEvent, matchId);
        List<PullUpSeries> pullUpSeries = new ArrayList<>();
        pullUpSeriesAddDto.stream().forEach(pullUpSeriesDto -> pullUpSeries.add(pullUpResultMapper.map(pullUpSeriesDto,pullUpMatch)));
        pullUpSeries.stream().forEach(pullUpSerie -> pullUpSeriesRepository.save(pullUpSerie));
        pullUpMatch.setPullUpSeries(pullUpSeries);
        setApproveAndNotifications(loggedUser, pullUpMatch,eventId, organizationId);
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