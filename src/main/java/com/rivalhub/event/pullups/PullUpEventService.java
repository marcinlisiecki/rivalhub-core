package com.rivalhub.event.pullups;

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
import com.rivalhub.user.UserDetailsDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PullUpEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final PullUpEventRepository pullUpEventRepository;
    private final PullUpEventSaver pullUpEventSaver;
    private final EventCommonService eventCommonService;

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        PullUpEvent pullUpEvent = new PullUpEvent();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(pullUpEventSaver.saveEvent(pullUpEvent, organization, eventDto));

    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getPullUpsEvents()
                .stream()
                .map(mapToEventDTO(organization))
                .toList();
    }

    private Function<PullUpEvent, EventDto> mapToEventDTO(Organization organization) {
        return pullUpEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(pullUpEvent);
            eventDto.setIsEventPublic(pullUpEvent.isEventPublic());
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(pullUpEvent, eventDto);
            return eventDto;
        };
    }

    @Override
    public EventDto findEvent(long eventId) {
        PullUpEvent event = pullUpEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventDto eventDto = autoMapper.mapToEventDto(event);
        eventDto.setIsEventPublic(event.isEventPublic());

        return eventDto;
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(pullUpEventRepository, id);
    }
    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.PULL_UPS.name());
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(pullUpEventRepository,eventId,userId);
    }

    @Override
    public List<UserDetailsDto> addUserToEvent(Long eventId, Long userId) {
        return eventCommonService.addUserToEvent(pullUpEventRepository, eventId, userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
        eventCommonService.joinPublicEvent(pullUpEventRepository, id);
    }

    @Override
    @Transactional
    public void deleteEvent(Long organizationId,Long eventId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        organization.getPullUpsEvents().remove(pullUpEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new));
        pullUpEventRepository.deleteById(eventId);
    }


}
