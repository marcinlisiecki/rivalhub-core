package com.rivalhub.event.darts;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.EventIsNotPublicException;
import com.rivalhub.event.EventDto;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.common.EventCommonService;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DartEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final DartEventRepository dartEventRepository;
    private final DartEventSaver dartEventSaver;
    private final EventCommonService eventCommonService;

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
                .map(dartEvent -> {
                    EventDto eventDto = autoMapper.mapToEventDto(dartEvent);
                    eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));
                    return eventDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventDto findEvent(long eventId) {
        return dartEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
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
    public void joinPublicEvent(Long id) {
       eventCommonService.joinPublicEvent(dartEventRepository, id);
    }
}
