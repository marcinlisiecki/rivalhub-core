package com.rivalhub.event.tablefootball.match;

import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventType;
import com.rivalhub.event.MatchNotFoundException;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.event.tablefootball.TableFootballEventRepository;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
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

    @Override
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        TableFootballEvent tableFootballEvent = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, tableFootballEvent, matchId, approve);

    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        TableFootballEvent tableFootballEvent = tableFootballEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        TableFootballMatch tableFootballMatch = tableFootballMatchMapper.map(MatchDTO, organization);

        return save(requestUser, tableFootballEvent, tableFootballMatch);
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
        TableFootballEvent tableFootballEvent = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        TableFootballMatch tableFootballMatch = findMatchInEvent(tableFootballEvent, matchId);

        addTableFootballSetsIn(tableFootballMatch, sets);
        TableFootballMatch savedMatch = tableFootballMatchRepository.save(tableFootballMatch);

        return savedMatch.getSets();
    }



    private MatchDto save(UserData loggedUser, TableFootballEvent tableFootballEvent,
                          TableFootballMatch tableFootballMatch){
        boolean loggedUserInTeam1 = tableFootballMatch.getTeam1()
                .stream().anyMatch(loggedUser::equals);

        if (loggedUserInTeam1) tableFootballMatch.setTeam1Approval(true);

        boolean loggedUserInTeam2 = tableFootballMatch.getTeam2()
                .stream().anyMatch(loggedUser::equals);

        if (loggedUserInTeam2) tableFootballMatch.setTeam2Approval(true);

        addTableFootballMatch(tableFootballEvent, tableFootballMatch);

        TableFootballMatch savedMatch = tableFootballMatchRepository.save(tableFootballMatch);
        tableFootballEventRepository.save(tableFootballEvent);

        return tableFootballMatchMapper.mapToMatchDto(savedMatch);
    }
    private void addTableFootballSetsIn(TableFootballMatch tableFootballMatch, List<TableFootballMatchSet> sets) {
        tableFootballMatch.getSets().addAll(sets);
    }

    private boolean setResultApproval(UserData loggedUser, TableFootballEvent tableFootballEvent, Long matchId, boolean approve) {
        TableFootballMatch tableFootballMatch = tableFootballEvent.getTableFootballMatch()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);

        if(tableFootballMatch.getTeam1().stream().anyMatch(loggedUser::equals)) tableFootballMatch.setTeam1Approval(approve);
        if(tableFootballMatch.getTeam2().stream().anyMatch(loggedUser::equals)) tableFootballMatch.setTeam2Approval(approve);

        tableFootballMatchRepository.save(tableFootballMatch);
        return approve;
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
