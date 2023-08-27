package com.rivalhub.event.running;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.event.common.EventCommonService;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunningEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final RunningEventRepository runningEventRepository;
    private final RunningEventSaver runningEventSaver;
    private final EventCommonService eventCommonService;

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        RunningEvent runningEvent = new RunningEvent();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(runningEventSaver.saveEvent(runningEvent, organization, eventDto));

    }

    @Override
    public List<EventDto> findAllEvents(long id) {

        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getRunningEvents()
                .stream()
                .map(runningEvent -> {
                    EventDto eventDto = autoMapper.mapToEventDto(runningEvent);
                    eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));
                    return eventDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventDto findEvent(long eventId) {
        return runningEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(runningEventRepository, id);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.RUNNING.name());
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(runningEventRepository,eventId,userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
        eventCommonService.joinPublicEvent(runningEventRepository, id);
    }
}
