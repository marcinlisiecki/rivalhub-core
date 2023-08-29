package com.rivalhub.event.darts;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.EventIsNotPublicException;
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
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserDetailsDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DartEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final DartEventRepository dartEventRepository;
    private final DartEventSaver dartEventSaver;
    private final EventCommonService eventCommonService;
    private final ReservationRepository reservationRepository;

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        DartEvent dartEvent = new DartEvent();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(dartEventSaver.saveEvent(dartEvent, organization, eventDto));

    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getDartEvents()
                .stream()
                .map(mapToEventDTO(organization))
                .toList();
    }

    private Function<DartEvent, EventDto> mapToEventDTO(Organization organization) {
        return dartEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(dartEvent);
            eventDto.setIsEventPublic(dartEvent.isEventPublic());
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(dartEvent, eventDto);
            return eventDto;
        };
    }

    @Override
    public EventDto findEvent(long eventId) {
        DartEvent event = dartEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventDto eventDto = autoMapper.mapToEventDto(event);
        eventDto.setIsEventPublic(event.isEventPublic());

        return eventDto;
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(dartEventRepository, id);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.DARTS.name());
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(dartEventRepository,eventId,userId);
    }

    @Override
    public List<UserDetailsDto> addUserToEvent(Long eventId, Long userId) {
        return eventCommonService.addUserToEvent(dartEventRepository, eventId, userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
       eventCommonService.joinPublicEvent(dartEventRepository, id);
    }

    @Override
    @Transactional
    public void deleteEvent(Long organizationId,Long eventId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        DartEvent eventToDelete = dartEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        organization.getDartEvents().remove(eventToDelete);

        reservationRepository.deleteById(eventToDelete.getReservationId());
        dartEventRepository.deleteById(eventId);
    }
}
