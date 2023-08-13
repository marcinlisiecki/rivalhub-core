package com.rivalhub.event.pingpong;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventServiceInterface;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PingPongService implements EventServiceInterface {

    private final AutoMapper autoMapper;
    private final RepositoryManager repositoryManager;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongEventSaver pingPongEventSaver;

    @Override
    @Transactional
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        PingPongEvent pingPongEvent = autoMapper.mapToPingPongEvent(eventDto);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        PingPongEvent savedEvent = pingPongEventSaver.saveEvent(pingPongEvent, organization, eventDto);
        return autoMapper.mapToEventDto(savedEvent);
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        Organization organization = repositoryManager.findOrganizationById(id);
        return pingPongEventRepository.findAllByOrganization(organization)
                .stream()
                .map(autoMapper::mapToEventDto)
                .collect(Collectors.toList());
    }


    public EventDto findEvent(long eventId) {
        return pingPongEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
    }
}
