package com.rivalhub.event.billiards;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventServiceInterface;
import com.rivalhub.event.EventType;
import com.rivalhub.event.match.MatchDto;
import com.rivalhub.event.match.MatchServiceInterface;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BilliardsService implements MatchServiceInterface {
    private final AutoMapper autoMapper;
    private final RepositoryManager repositoryManager;
    private final BilliardsEventRepository billiardsEventRepository;
    private final BilliardsEventSaver billiardsEventSaver;

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
