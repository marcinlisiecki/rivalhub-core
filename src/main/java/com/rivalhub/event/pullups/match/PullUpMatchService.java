package com.rivalhub.event.pullups.match;

import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchService;
import com.rivalhub.event.match.MatchServiceInterface;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.event.pingpong.match.PingPongMatchService;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.pullups.PullUpEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PullUpMatchService implements MatchServiceInterface {

    private final OrganizationRepository organizationRepository;
    private final PullUpEventRepository pullUpEventRepository;

    @Override
    public boolean setResultApproval(Long eventId, Long matchId, boolean approve) {
        return false;
    }

    @Override
    public MatchDto createMatch(Long organizationId, Long eventId, MatchDto MatchDTO) {

        var requestUser = SecurityUtils.getUserFromSecurityContext();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        PullUpEvent pullUpEvent = pullUpEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        PullUpMatch pullUpMatch = pingPongMatchMapper.map(MatchDTO, organization);

        return save(requestUser, pullUpEvent, pullUpMatch);
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
