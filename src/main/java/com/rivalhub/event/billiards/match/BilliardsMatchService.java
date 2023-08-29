package com.rivalhub.event.billiards.match;

import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.common.exception.MatchNotFoundException;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.billiards.BilliardsEventRepository;
import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.darts.match.DartMatch;
import com.rivalhub.event.darts.match.result.DartRound;
import com.rivalhub.event.darts.match.result.Leg;
import com.rivalhub.event.darts.match.result.SinglePlayerScoreInRound;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BilliardsMatchService implements MatchService {
    private final OrganizationRepository organizationRepository;
    private final BilliardsMatchRepository billiardsMatchRepository;
    private final BilliardsEventRepository billiardsEventRepository;
    private final BilliardsMatchMapper billiardsMatchMapper;
    @Override
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        throw new NotImplementedException();
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        BilliardsEvent pingPongEvent = billiardsEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        BilliardsMatch billiardsMatch = billiardsMatchMapper.map(MatchDTO, organization);

        return save(requestUser, pingPongEvent, billiardsMatch);
    }

    @Override
    public ViewMatchDto findMatch(Long eventId, Long matchId) {
        BilliardsEvent billiardsEvent = billiardsEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        BilliardsMatch billiardsMatch = findMatchInEvent(billiardsEvent, matchId);

        return billiardsMatchMapper.map(billiardsMatch);
    }

    private BilliardsMatch findMatchInEvent(BilliardsEvent billiardsEvent, Long matchId) {
        return billiardsEvent.getBilliardsMatches()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }

    @Override
    public List<ViewMatchDto> findMatches(Long eventId) {
        BilliardsEvent billiardsEvent = billiardsEventRepository.
                findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        List<BilliardsMatch> billiardsMatchList = billiardsEvent.getBilliardsMatches();

        return new ArrayList<ViewMatchDto>(billiardsMatchList.stream().map(billiardsMatchMapper::map).toList());
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.BILLIARDS.name());
    }

    private MatchDto save(UserData loggedUser, BilliardsEvent billiardsEvent,
                          BilliardsMatch billiardsMatch){

        addBilliardsMatch(billiardsEvent, billiardsMatch);

        BilliardsMatch savedMatch = billiardsMatchRepository.save(billiardsMatch);
        billiardsEventRepository.save(billiardsEvent);

        return billiardsMatchMapper.mapToMatchDto(savedMatch);
    }

    private void addBilliardsMatch(BilliardsEvent billiardsEvent, BilliardsMatch billiardsMatch) {
        billiardsEvent.getBilliardsMatches().add(billiardsMatch);
    }

    public ViewMatchDto addResult(Long eventId, Long matchId, BilliardsMatchResultAdd billiardsMatchResultAdd) {
        BilliardsEvent billiardsEvent = billiardsEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        BilliardsMatch billiardsMatch = findMatchInEvent(billiardsEvent, matchId);
        setResults(billiardsMatch,billiardsMatchResultAdd);
        BilliardsMatch savedMatch = billiardsMatchRepository.save(billiardsMatch);

        return billiardsMatchMapper.map(savedMatch);
    }

    private void setResults(BilliardsMatch billiardsMatch,BilliardsMatchResultAdd billiardsMatchResultAdd){
        billiardsMatch.setWinType(billiardsMatchResultAdd.getWinType());
        billiardsMatch.setHowManyBillsLeftTeam2(billiardsMatchResultAdd.getHowManyBillsLeftTeam2());
        billiardsMatch.setHowManyBillsLeftTeam1(billiardsMatchResultAdd.getHowManyBillsLeftTeam1());
        billiardsMatch.setTeam1PlaysFull(billiardsMatchResultAdd.isTeam1PlaysFull());
        billiardsMatch.setTeam1HadPottedFirst(billiardsMatchResultAdd.isTeam1HadPottedFirst());
        billiardsMatch.setTeam1Won(billiardsMatchResultAdd.isTeam1Won());
        billiardsMatch.setTeam2Won(billiardsMatchResultAdd.isTeam2Won());
    }

    public void deleteBilliardsMatch(Long eventId, Long matchId) {
        BilliardsEvent billiardsEvent = billiardsEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        BilliardsMatch billiardsMatchToDelete = billiardsEvent.getBilliardsMatches().stream().filter(billiardsMatch -> billiardsMatch.getId() == matchId).findFirst().orElseThrow(MatchNotFoundException::new);
        billiardsEvent.getBilliardsMatches().remove(billiardsMatchToDelete);
        billiardsEventRepository.save(billiardsEvent);
        billiardsMatchRepository.delete(billiardsMatchToDelete);
    }
}
