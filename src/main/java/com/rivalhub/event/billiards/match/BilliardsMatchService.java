package com.rivalhub.event.billiards.match;

import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.MatchServiceInterface;
import com.rivalhub.event.match.ViewMatchDto;

import java.util.List;

public class BilliardsMatchService implements MatchServiceInterface {
    @Override
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        return false;
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {
        return null;
    }

    @Override
    public ViewMatchDto findMatch(Long eventId, Long matchId) {
        return null;
    }

    @Override
    public List<ViewMatchDto> findMatches(Long eventId) {
        return null;
    }

    @Override
    public boolean matchStrategy(String strategy) {
        return false;
    }
}
