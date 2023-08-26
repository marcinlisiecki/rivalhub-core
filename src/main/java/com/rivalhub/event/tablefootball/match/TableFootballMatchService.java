package com.rivalhub.event.tablefootball.match;

import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventType;
import com.rivalhub.common.exception.MatchNotFoundException;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.event.tablefootball.TableFootballEventRepository;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
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

@RequiredArgsConstructor
@Service
public class TableFootballMatchService implements MatchService {

    private final OrganizationRepository organizationRepository;
    private final TableFootballEventRepository tableFootballEventRepository;
    private final TableFootballMatchRepository tableFootballMatchRepository;
    private final TableFootballMatchMapper tableFootballMatchMapper;
    private final UserRepository userRepository;

    @Override
    public boolean setResultApproval(Long eventId, Long matchId) {

        var requestUser = SecurityUtils.getUserFromSecurityContext();
        TableFootballEvent tableFootballEvent = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, tableFootballEvent, matchId);
    }

    private boolean setResultApproval(UserData loggedUser, TableFootballEvent tableFootballEvent, Long matchId) {
        TableFootballMatch tableFootballMatch = tableFootballEvent.getTableFootballMatch()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, tableFootballMatch);
        tableFootballMatchRepository.save(tableFootballMatch);
        return tableFootballMatch.getUserApprovalMap().get(loggedUser.getId());
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

        addTableFootballSetsIn(tableFootballMatch, sets);

        setApproveAndNotifications(loggedUser,tableFootballMatch, eventId);
        TableFootballMatch savedMatch = tableFootballMatchRepository.save(tableFootballMatch);

        return savedMatch.getSets();
    }

    private void setApproveAndNotifications(UserData loggedUser, TableFootballMatch tableFootballMatch, Long eventId) {
        tableFootballMatch.getUserApprovalMap().put(loggedUser.getId(),true);
        tableFootballMatch.getTeam1()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId())
                .forEach(userData -> saveNotification(userData,EventType.PING_PONG, tableFootballMatch.getId(), eventId));
        tableFootballMatch.getTeam2()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId())
                .forEach(userData -> saveNotification(userData,EventType.PING_PONG, tableFootballMatch.getId(), eventId));
    }

    private void saveNotification(UserData userData, EventType type, Long matchId, Long eventId) {
        userData.getNotifications().add(
                new Notification(eventId, matchId, type, Notification.Status.NOT_CONFIRMED));
        userRepository.save(userData);
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

}
