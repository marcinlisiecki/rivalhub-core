package com.rivalhub.event.pullups.match;

import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.common.exception.MatchNotFoundException;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.event.EventType;
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
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.pullups.PullUpEventRepository;
import com.rivalhub.event.pullups.match.result.*;
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
public class PullUpMatchService implements MatchService {

    private final OrganizationRepository organizationRepository;
    private final PullUpEventRepository pullUpEventRepository;
    private final PullUpMatchRepository pullUpMatchRepository;
    private final PullUpMatchMapper pullUpMatchMapper;
    private final PullUpResultMapper pullUpResultMapper;
    private final PullUpSeriesRepository pullUpSeriesRepository;

    @Override
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        throw new NotImplementedException();
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {

        var requestUser = SecurityUtils.getUserFromSecurityContext();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        PullUpEvent pullUpEvent = pullUpEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PullUpMatch pullUpMatch = pullUpMatchMapper.map(MatchDTO, organization);

        return save(requestUser, pullUpEvent, pullUpMatch);

    }

    private MatchDto save(UserData loggedUser, PullUpEvent pullUpEvent,
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
        PullUpEvent pullUpEvent = pullUpEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        PullUpMatch pullUpMatch = findMatchInEvent(pullUpEvent, matchId);
        List<PullUpSeries> pullUpSeries = new ArrayList<>();
        pullUpSeriesAddDto.stream().forEach(pullUpSeriesDto -> pullUpSeries.add(pullUpResultMapper.map(pullUpSeriesDto,pullUpMatch)));
        pullUpSeries.stream().forEach(pullUpSerie -> pullUpSeriesRepository.save(pullUpSerie));
        pullUpMatch.setPullUpSeries(pullUpSeries);


        PullUpMatch savedMatch = pullUpMatchRepository.save(pullUpMatch);

        return pullUpMatchMapper.map(savedMatch);
    }
}
