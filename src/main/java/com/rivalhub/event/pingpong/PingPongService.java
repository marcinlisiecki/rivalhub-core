package com.rivalhub.event.pingpong;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.common.exception.OrganizationNotFoundException;
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

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        PingPongEvent pingPongEvent = new PingPongEvent();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(pingPongEventSaver.saveEvent(pingPongEvent, organization, eventDto));
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepoManager.getOrganizationWithPingPongEventsById(id);

        return organization.getPingPongEvents()
                .stream()
                .map(mapEventToDTO(organization))
                .collect(Collectors.toList());
    }

    private Function<PingPongEvent, EventDto> mapEventToDTO(Organization organization) {
        return pingPongEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(pingPongEvent);
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            if (pingPongEvent.getEndTime().isAfter(LocalDateTime.now())
                    &&
                    pingPongEvent.getStartTime().isAfter(LocalDateTime.now())
            ) eventDto.setStatus("Incoming");
            else if (pingPongEvent.getStartTime().isBefore(LocalDateTime.now())
                    &&
                    pingPongEvent.getEndTime().isBefore(LocalDateTime.now())) eventDto.setStatus("Historical");
            else eventDto.setStatus("Active");

            return eventDto;
        };
    }

    public EventDto findEvent(long eventId) {
        return pingPongEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
    }

    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.PING_PONG.name());
    }
}
