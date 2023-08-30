package com.rivalhub.event.tablefootball.match;

import com.rivalhub.common.exception.*;
import com.rivalhub.event.EventType;
import com.rivalhub.event.match.MatchApprovalService;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.event.tablefootball.TableFootballEventRepository;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
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

@RequiredArgsConstructor
@Service
public class TableFootballMatchService implements MatchService {

    private final OrganizationRepository organizationRepository;
    private final TableFootballEventRepository tableFootballEventRepository;
    private final TableFootballMatchRepository tableFootballMatchRepository;
    private final TableFootballMatchMapper tableFootballMatchMapper;
    private final UserRepository userRepository;
    private final StatsRepository statsRepository;

    @Override
    public boolean setResultApproval(Long eventId, Long matchId, Long organizationId) {

        var requestUser = SecurityUtils.getUserFromSecurityContext();
        TableFootballEvent tableFootballEvent = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, tableFootballEvent, matchId, organizationId);
    }

    private boolean setResultApproval(UserData loggedUser, TableFootballEvent tableFootballEvent, Long matchId, Long organizationId) {
        TableFootballMatch tableFootballMatch = tableFootballEvent.getTableFootballMatch()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, tableFootballMatch);
        if(tableFootballMatchMapper.isApprovedByDemanded(tableFootballMatch)){
            addStats(organizationId, tableFootballMatch);
            MatchApprovalService.findNotificationToDisActivate(tableFootballMatch.getTeam2(), matchId, EventType.TABLE_FOOTBALL, userRepository);
            MatchApprovalService.findNotificationToDisActivate(tableFootballMatch.getTeam1(), matchId, EventType.TABLE_FOOTBALL, userRepository);
        } else {
            MatchApprovalService.findNotificationToDisActivate(findUserTeam(tableFootballMatch, loggedUser), matchId, EventType.TABLE_FOOTBALL, userRepository);
        }
        tableFootballMatchRepository.save(tableFootballMatch);
        return tableFootballMatch.getUserApprovalMap().get(loggedUser.getId());
    }

    private void addStats(Long organizationId, TableFootballMatch tableFootballMatch) {
        List<UserData> matchParticipants = Stream.concat(tableFootballMatch.getTeam1().stream(), tableFootballMatch.getTeam2()
                        .stream())
                .toList();
        List<Stats> statsList = statsRepository.findByUserAndOrganization(organizationId, matchParticipants);


        matchParticipants.forEach(userData -> statsList.forEach(stats -> {
            if (stats.getUserData().equals(userData)) {
                stats.setGamesInTableFootBall(stats.getGamesInTableFootBall() + 1);
                if (checkIfUserWon(userData, tableFootballMatch)) {
                    stats.setWinInTableFootBall(stats.getWinInTableFootBall() + 1);
                }
            }
        }));

        statsRepository.saveAll(statsList);
    }

    private boolean checkIfUserWon(UserData user, TableFootballMatch tableFootballMatch) {
        int team1WonSets = 0;
        int team2WonSets = 0;

        List<TableFootballMatchSet> sets = tableFootballMatch.getSets();

        for (TableFootballMatchSet set : sets) {
            if (set.getTeam1Score() > set.getTeam2Score()) {
                team1WonSets += 1;
            } else if (set.getTeam1Score() < set.getTeam2Score()) {
                team2WonSets += 1;
            }
        }

        if (team1WonSets > team2WonSets && tableFootballMatch.getTeam1().contains(user)) {
            return true;
        }
        if (team2WonSets > team1WonSets && tableFootballMatch.getTeam2().contains(user)) {
            return true;
        }

        return false;
    }

    private void setApprove(UserData loggedUser, TableFootballMatch tableFootballMatch) {
        tableFootballMatch.getUserApprovalMap().replace(loggedUser.getId(), !(tableFootballMatch.getUserApprovalMap().get(loggedUser.getId())));
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        TableFootballEvent tableFootballEvent = tableFootballEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        TableFootballMatch tableFootballMatch = tableFootballMatchMapper.map(MatchDTO, organization);

        return save(tableFootballEvent, tableFootballMatch);
    }

    @Override
    public ViewMatchDto findMatch(Long eventId, Long matchId) {
        TableFootballEvent tableFootballEvent = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        TableFootballMatch tableFootballMatch = findMatchInEvent(tableFootballEvent, matchId);

        return tableFootballMatchMapper.map(tableFootballMatch);
    }

    @Override
    public List<ViewMatchDto> findMatches(Long eventId) {
        TableFootballEvent tableFootballEvent = tableFootballEventRepository.
                findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        List<TableFootballMatch> tableFootballMatches = tableFootballEvent.getTableFootballMatch();

        return new ArrayList<ViewMatchDto>(tableFootballMatches.stream().map(tableFootballMatchMapper::map).toList());
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.TABLE_FOOTBALL.name());
    }

    public List<TableFootballMatchSet> addResult(Long eventId, Long matchId, List<TableFootballMatchSet> sets) {
        var loggedUser = SecurityUtils.getUserFromSecurityContext();
        TableFootballEvent tableFootballEvent = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        TableFootballMatch tableFootballMatch = findMatchInEvent(tableFootballEvent, matchId);

        setApproveAndNotifications(loggedUser,tableFootballMatch, eventId);
        //MatchApprovalService.findNotificationToDisActivate(findUserTeam(tableFootballMatch, loggedUser), matchId, EventType.TABLE_FOOTBALL, userRepository);

        addTableFootballSetsIn(tableFootballMatch, sets);



        TableFootballMatch savedMatch = tableFootballMatchRepository.save(tableFootballMatch);

        return savedMatch.getSets();
    }


    private List<UserData> findUserTeam(TableFootballMatch tableFootballMatch, UserData loggedUser) {
        if(tableFootballMatch.getTeam1().stream().anyMatch(userData -> userData.getId() == loggedUser.getId())){
            return tableFootballMatch.getTeam1();
        }
        if(tableFootballMatch.getTeam2().stream().anyMatch(userData -> userData.getId() == loggedUser.getId())){
            return tableFootballMatch.getTeam2();
        }
        throw new UserNotFoundException();
    }
    private void setApproveAndNotifications(UserData loggedUser, TableFootballMatch tableFootballMatch, Long eventId) {
        tableFootballMatch.getUserApprovalMap().keySet().forEach(key -> tableFootballMatch.getUserApprovalMap().put(key,false));
        tableFootballMatch.getUserApprovalMap().put(loggedUser.getId(),true);
        if(loggedUser.getNotifications().stream().anyMatch(notification -> notification.getType() == EventType.TABLE_FOOTBALL && notification.getMatchId() == tableFootballMatch.getId())) {
            loggedUser.getNotifications().stream().filter(notification -> notification.getType() == EventType.TABLE_FOOTBALL && notification.getMatchId() == tableFootballMatch.getId()).findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.CONFIRMED);
            userRepository.save(loggedUser);
        }
        tableFootballMatch.getTeam1()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId() && userData.getNotifications().stream().noneMatch(notification -> notification.getType() == EventType.TABLE_FOOTBALL && notification.getMatchId() == tableFootballMatch.getId()))
                .forEach(userData -> saveNotification(userData,EventType.TABLE_FOOTBALL, tableFootballMatch.getId(), eventId));
        tableFootballMatch.getTeam2()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId() && userData.getNotifications().stream().noneMatch(notification -> notification.getType() == EventType.TABLE_FOOTBALL && notification.getMatchId() == tableFootballMatch.getId()))
                .forEach(userData -> saveNotification(userData,EventType.TABLE_FOOTBALL, tableFootballMatch.getId(), eventId));
        tableFootballMatch.getTeam1()
                .stream()
                .filter(userData -> (userData.getId() != loggedUser.getId()))
                .forEach(userData ->{
                    userData.getNotifications()
                            .stream()
                            .filter(notification -> notification.getType() == EventType.TABLE_FOOTBALL && notification.getMatchId() == tableFootballMatch.getId())
                            .findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.NOT_CONFIRMED);
                    userRepository.save(userData);
                });

        tableFootballMatch.getTeam2()
                .stream()
                .filter(userData -> (userData.getId() != loggedUser.getId()))
                .forEach(userData ->{
                    userData.getNotifications()
                            .stream()
                            .filter(notification -> notification.getType() == EventType.TABLE_FOOTBALL && notification.getMatchId() == tableFootballMatch.getId())
                            .findFirst().orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.NOT_CONFIRMED);
                    userRepository.save(userData);
                });
    }

    private void saveNotification(UserData userData, EventType type, Long matchId, Long eventId) {
        MatchApprovalService.saveNotification( userData,type, matchId, eventId,userRepository);
    }

    private MatchDto save(TableFootballEvent tableFootballEvent,
                          TableFootballMatch tableFootballMatch){

        addTableFootballMatch(tableFootballEvent, tableFootballMatch);

        TableFootballMatch savedMatch = tableFootballMatchRepository.save(tableFootballMatch);
        tableFootballEventRepository.save(tableFootballEvent);

        return tableFootballMatchMapper.mapToMatchDto(savedMatch);
    }

    private void addTableFootballSetsIn(TableFootballMatch tableFootballMatch, List<TableFootballMatchSet> sets) {
        tableFootballMatch.getSets().addAll(sets);
    }


    private void addTableFootballMatch(TableFootballEvent tableFootballEvent,TableFootballMatch tableFootballMatch){
        tableFootballEvent.getTableFootballMatch().add(tableFootballMatch);
    }

    private TableFootballMatch findMatchInEvent(TableFootballEvent tableFootballEvent, Long matchId){
        return tableFootballEvent.getTableFootballMatch()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }

    public void deleteTableFootballSet(Long eventId, Long matchId, TableFootballMatchSet tableFootballSet) {
        TableFootballEvent tableFootballEvent = tableFootballEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        TableFootballMatch match = findMatchInEvent(tableFootballEvent, matchId);

        if (match.getSets().isEmpty()) return;
        if (match.getUserApprovalMap().containsValue(false));

        match.getSets()
                .forEach(set -> {
                    if (set.getSetNr() > tableFootballSet.getSetNr()) {
                        set.setSetNr(set.getSetNr() - 1);
                    }
                });

        tableFootballMatchRepository.save(match);
    }


}
