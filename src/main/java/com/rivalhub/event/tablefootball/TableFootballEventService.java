package com.rivalhub.event.tablefootball;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableFootballEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final TableFootballEventRepository tableFootballEventRepository;
    private final TableFootballEventSaver tableFootballEventSaver;


    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        TableFootballEvent tableFootballEvent = new TableFootballEvent();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(tableFootballEventSaver.saveEvent(tableFootballEvent, organization, eventDto));

    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getTableFootballEvents()
                .stream()
                .map(tableFootballEvent -> {
                    EventDto eventDto = autoMapper.mapToEventDto(tableFootballEvent);
                    eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));
                    return eventDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventDto findEvent(long eventId) {
        return tableFootballEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);

    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.TABLE_FOOTBALL.name());
    }
}
