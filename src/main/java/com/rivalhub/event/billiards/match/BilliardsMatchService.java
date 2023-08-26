package com.rivalhub.event.billiards.match;

import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.ViewMatchDto;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class BilliardsMatchService implements MatchService {
    @Override
    public boolean setResultApproval(Long eventId, Long matchId) {
        throw new NotImplementedException();
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {
        throw new NotImplementedException();
    }

    @Override
    public ViewMatchDto findMatch(Long eventId, Long matchId) {
        throw new NotImplementedException();
    }

    @Override
    public List<ViewMatchDto> findMatches(Long eventId) {
        throw new NotImplementedException();
    }

    @Override
    public boolean matchStrategy(String strategy) {
        throw new NotImplementedException();
    }
}
