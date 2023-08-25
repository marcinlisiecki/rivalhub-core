package com.rivalhub.event.tablefootball;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.event.common.EventCommonService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableFootballEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final TableFootballEventRepository tableFootballEventRepository;
    private final TableFootballEventSaver tableFootballEventSaver;
    private final EventCommonService eventCommonService;

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
                .map(mapToDTO(organization))
                .toList();
    }

    private Function<TableFootballEvent, EventDto> mapToDTO(Organization organization) {
        return tableFootballEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(tableFootballEvent);
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(tableFootballEvent, eventDto);
            return eventDto;
        };
    }

    @Override
    public EventDto findEvent(long eventId) {
        return tableFootballEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);

    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(tableFootballEventRepository, id);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.TABLE_FOOTBALL.name());
    }
}
