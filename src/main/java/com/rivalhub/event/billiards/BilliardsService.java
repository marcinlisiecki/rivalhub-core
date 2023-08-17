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
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BilliardsService implements EventServiceInterface {
    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final BilliardsEventRepository billiardsEventRepository;
    private final BilliardsEventSaver billiardsEventSaver;


    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        BilliardsEvent billiardsEvent = new BilliardsEvent();
        var organization  = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(billiardsEventSaver.saveEvent(billiardsEvent,organization,eventDto));
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getBilliardsEvents()
                .stream()
                .map(billiardsEvent -> {
                    EventDto eventDto = autoMapper.mapToEventDto(billiardsEvent);
                    eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));
                    return eventDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventDto findEvent(long eventId) {
        return billiardsEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.BILLIARDS.name());
    }
}
