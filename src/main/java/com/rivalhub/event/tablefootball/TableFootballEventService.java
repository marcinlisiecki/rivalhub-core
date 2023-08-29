package com.rivalhub.event.tablefootball;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.common.EventCommonService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.ReservationRepository;
import com.rivalhub.user.UserDetailsDto;
import jakarta.transaction.Transactional;
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
    private final ReservationRepository reservationRepository;

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
            eventDto.setIsEventPublic(tableFootballEvent.isEventPublic());
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(tableFootballEvent, eventDto);
            return eventDto;
        };
    }

    @Override
    public EventDto findEvent(long eventId) {
        TableFootballEvent event = tableFootballEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventDto eventDto = autoMapper.mapToEventDto(event);
        eventDto.setIsEventPublic(event.isEventPublic());

        return eventDto;
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(tableFootballEventRepository, id);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.TABLE_FOOTBALL.name());
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(tableFootballEventRepository,eventId,userId);
    }

    @Override
    public List<UserDetailsDto> addUserToEvent(Long eventId, Long userId) {
        return eventCommonService.addUserToEvent(tableFootballEventRepository, eventId, userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
        eventCommonService.joinPublicEvent(tableFootballEventRepository, id);
    }

    @Override
    @Transactional
    public void deleteEvent(Long organizationId,Long eventId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        TableFootballEvent eventToDelete = tableFootballEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        organization.getTableFootballEvents().remove(eventToDelete);

        reservationRepository.deleteById(eventToDelete.getEventId());
        tableFootballEventRepository.deleteById(eventId);
    }
}
