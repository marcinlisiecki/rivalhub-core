package com.rivalhub.event.running;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventServiceInterface;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunningEventService implements EventServiceInterface {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final RunningEventRepository runningEventRepository;
    private final RunningEventSaver runningEventSaver;


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
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.RUNNING.name());
    }
}
