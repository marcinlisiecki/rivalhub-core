package com.rivalhub.event.match;

import com.rivalhub.event.pingpong.match.ViewPingPongMatchDTO;

import java.util.List;

public interface MatchServiceInterface {
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve);

    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO);

    public ViewMatchDto findMatch(Long eventId, Long matchId);

    public List<ViewMatchDto> findMatches(Long eventId);
    boolean matchStrategy(String strategy);
}
