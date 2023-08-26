package com.rivalhub.event.darts.match;


import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.common.exception.MatchNotFoundException;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.event.EventType;

import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.darts.DartEventRepository;
import com.rivalhub.event.darts.match.result.*;
import com.rivalhub.event.darts.match.result.LegRepository;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.notification.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DartMatchService implements MatchService {

    //WIENCEJ REPOZYTORIÓW SPRING WYCZYMA
    final OrganizationRepository organizationRepository;
    final DartEventRepository dartEventRepository;
    final DartMatchRepository dartMatchRepository;
    final DartMatchMapper dartMatchMapper;
    final DartResultMapper dartResultMapper;
    final LegRepository legRepository;
    final DartRoundRepository dartRoundRepository;
    final SinglePlayerInRoundRepository singlePlayerInRoundRepository;
    final UserRepository userRepository;
    @Override
    public boolean setResultApproval(Long eventId, Long matchId) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        DartEvent dartEvent = dartEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return setResultApproval(requestUser, dartEvent, matchId);
    }

    private boolean setResultApproval(UserData loggedUser, DartEvent dartEvent, Long matchId) {
        DartMatch dartMatch = dartEvent.getDartsMatch()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
        setApprove(loggedUser, dartMatch);
        dartMatchRepository.save(dartMatch);
        return dartMatch.getUserApprovalMap().get(loggedUser.getId());
    }

    private void setApprove(UserData loggedUser, DartMatch pingPongMatch) {
        pingPongMatch.getUserApprovalMap().replace(loggedUser.getId(), !(pingPongMatch.getUserApprovalMap().get(loggedUser.getId())));
    }

    private void setApproveAndNotifications(UserData loggedUser, DartMatch dartMatch, Long eventId) {

        dartMatch.getUserApprovalMap().put(loggedUser.getId(),true);
        dartMatch.getParticipants()
                .stream()
                .filter(userData -> userData.getId() != loggedUser.getId())
                .forEach(userData -> saveNotification(userData,EventType.PING_PONG, dartMatch.getId(), eventId));
        }

    private void saveNotification(UserData userData, EventType type, Long matchId, Long eventId) {
        userData.getNotifications().add(
                new Notification(eventId, matchId, type, Notification.Status.NOT_CONFIRMED));
        userRepository.save(userData);
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        DartEvent dartEvent = dartEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        DartMatch dartMatch = dartMatchMapper.map(MatchDTO, organization);

        return save( dartEvent, dartMatch);
    }

    @Override
    public ViewMatchDto findMatch(Long eventId, Long matchId) {
        DartEvent dartEvent = dartEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        DartMatch dartMatch = findMatchInEvent(dartEvent, matchId);

        return dartMatchMapper.map(dartMatch);
    }

    @Override
    public List<ViewMatchDto> findMatches(Long eventId) {
        DartEvent dartEvent = dartEventRepository.
                findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        List<DartMatch> dartMatches = dartEvent.getDartsMatch();

        return new ArrayList<ViewMatchDto>(dartMatches.stream().map(dartMatchMapper::map).toList());

    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.DARTS.name());
    }

    private MatchDto save(DartEvent dartEvent, DartMatch dartMatch){

        addDartMatch(dartEvent, dartMatch);
        DartMatch savedMatch = dartMatchRepository.save(dartMatch);
        dartEventRepository.save(dartEvent);

        return dartMatchMapper.mapToMatchDto(savedMatch);
    }

    private void addDartMatch(DartEvent dartEvent,DartMatch dartMatch){
        dartEvent.getDartsMatch().add(dartMatch);
    }

    private DartMatch findMatchInEvent(DartEvent dartEvent, Long matchId){
        return dartEvent.getDartsMatch()
                .stream().filter(match -> match.getId().equals(matchId))
                .findFirst()
                .orElseThrow(MatchNotFoundException::new);
    }


    public ViewMatchDto addResult(Long eventId, Long matchId, List<LegAddDto> legListDto) {
        var loggedUser = SecurityUtils.getUserFromSecurityContext();
        DartEvent dartEvent = dartEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        DartMatch dartMatch = findMatchInEvent(dartEvent, matchId);
        setApproveAndNotifications(loggedUser, dartMatch, eventId);

        List<Leg> legList = legListDto.stream().map(dartResultMapper::map).toList();
        for (Leg leg:legList) {
            for (DartRound round: leg.getRoundList()) {
                int userNumber = 0;
                for(SinglePlayerScoreInRound singlePlayerScoreInRound : round.getSinglePlayerScoreInRoundsList()){
                    singlePlayerScoreInRound.setUserData(dartMatch.getParticipants().get(userNumber));
                    singlePlayerInRoundRepository.save(singlePlayerScoreInRound);
                }
            }
        }
        addLegsIn(dartMatch, legList);
        DartMatch savedMatch = dartMatchRepository.save(dartMatch);

        return dartMatchMapper.map(savedMatch);
    }

    private void addLegsIn(DartMatch dartMatch, List<Leg> legList) {
        dartMatch.getLegList().addAll(legList);
    }


}
