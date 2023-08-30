package com.rivalhub.event.pingpong;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.HostRemoveException;
import com.rivalhub.common.exception.UserNotFoundException;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.common.EventCommonService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.ReservationRepository;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.profile.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PingPongService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongEventSaver pingPongEventSaver;
    private final OrganizationRepoManager organizationRepoManager;
    private final EventCommonService eventCommonService;
    private final ReservationRepository reservationRepository;

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        PingPongEvent pingPongEvent = new PingPongEvent();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(pingPongEventSaver.saveEvent(pingPongEvent, organization, eventDto));
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        return organization.getPingPongEvents()
                .stream()
                .map(mapEventToDTO(organization))
                .toList();
    }

    private Function<PingPongEvent, EventDto> mapEventToDTO(Organization organization) {
        return pingPongEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(pingPongEvent);
            eventDto.setIsEventPublic(pingPongEvent.isEventPublic());
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(pingPongEvent, eventDto);
            return eventDto;
        };
    }

    public EventDto findEvent(long eventId) {
        PingPongEvent event = pingPongEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventDto eventDto = autoMapper.mapToEventDto(event);
        eventDto.setIsEventPublic(event.isEventPublic());

        return eventDto;
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(pingPongEventRepository, id);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.PING_PONG.name());
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(pingPongEventRepository, eventId, userId);
    }

    @Override
    public List<UserDetailsDto> addUserToEvent(Long eventId, Long userId) {
        return eventCommonService.addUserToEvent(pingPongEventRepository, eventId, userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
        eventCommonService.joinPublicEvent(pingPongEventRepository, id);
    }

    @Override
    @Transactional
    public void deleteEvent(Long organizationId,Long eventId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
        PingPongEvent eventToDelete = pingPongEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        organization.getPingPongEvents().remove(eventToDelete);

        reservationRepository.deleteById(eventToDelete.getReservationId());
        pingPongEventRepository.deleteById(eventId);
    }
}
