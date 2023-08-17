package com.rivalhub.event.billiards;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventServiceInterface;
import com.rivalhub.event.EventType;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BilliardsService implements EventServiceInterface {
    private final AutoMapper autoMapper;
    private final RepositoryManager repositoryManager;
    private final BilliardsEventRepository billiardsEventRepository;
    private final BilliardsEventSaver billiardsEventSaver;

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        BilliardsEvent billiardsEvent = new BilliardsEvent();
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        BilliardsEvent savedEvent = billiardsEventSaver.saveEvent(billiardsEvent, organization, eventDto);
        return autoMapper.mapToEventDto(savedEvent);
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        Organization organization = repositoryManager.findOrganizationById(id);
        return billiardsEventRepository.findAllByOrganization(organization)
                .stream()
                .map(autoMapper::mapToEventDto)
                .collect(Collectors.toList());
    }


    public EventDto findEvent(long eventId) {
        return billiardsEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
    }

    @Override
    public EventType getEventType() {
        return EventType.PING_PONG;
    }
}
