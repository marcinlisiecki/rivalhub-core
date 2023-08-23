package com.rivalhub.event.match;

import java.util.List;

public interface MatchService {
    boolean setResultApproval(Long eventId, Long matchId, boolean approve);

    MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO);

    ViewMatchDto findMatch(Long eventId, Long matchId);

    List<ViewMatchDto> findMatches(Long eventId);
    boolean matchStrategy(String strategy);
}
