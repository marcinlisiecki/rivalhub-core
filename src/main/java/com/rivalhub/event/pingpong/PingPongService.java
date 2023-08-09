package com.rivalhub.event.pingpong;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventNotFoundException;
import com.rivalhub.event.EventServiceInterface;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PingPongService implements EventServiceInterface {

    final AutoMapper autoMapper;
    final OrganizationRepository organizationRepository;
    final PingPongEventRepository pingPongEventRepository;

    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        OrganizationDTO organization = organizationRepository.findById(organizationId).map(autoMapper::mapToOrganizationDto).orElseThrow(OrganizationNotFoundException::new);
        eventDto.setOrganization(organization);
        PingPongEvent savedEvent = pingPongEventRepository.save(autoMapper.mapToPingPongEvent(eventDto));
        return autoMapper.mapToEventDto(savedEvent);
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        List<EventDto> eventDtos = new ArrayList<>();
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        eventDtos = pingPongEventRepository.findAllByOrganization(organization)
                .stream()
                .map(autoMapper::mapToEventDto)
                .collect(Collectors.toList());
        return eventDtos;
    }


    public EventDto findEvent(long eventId) {
        EventDto eventDto = pingPongEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
        return eventDto;
    }
}
