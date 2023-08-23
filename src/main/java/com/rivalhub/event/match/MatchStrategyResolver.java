package com.rivalhub.event.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchStrategyResolver {

    private final MatchOperator matchOperator;
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve , String type){
        return matchOperator.useStrategy(type).setResultApproval(eventId,matchId,approve);
    }

    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto matchDTO,String type){
        return matchOperator.useStrategy(type).createMatch(organizationId,eventId,matchDTO);
    };

    public ViewMatchDto findMatch(Long organizationId, Long eventId,String type){
        return matchOperator.useStrategy(type).findMatch(organizationId,eventId);
    };

    public List<ViewMatchDto> findMatches(Long EventId, String type){
      return matchOperator.useStrategy(type).findMatches(EventId);
    };



}
