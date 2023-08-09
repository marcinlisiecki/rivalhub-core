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
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.UserService;
import jakarta.transaction.Transactional;
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
    final UserRepository userRepository;

    @Override
    @Transactional
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        PingPongEvent pingPongEvent = autoMapper.mapToPingPongEvent(eventDto);
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        pingPongEvent.setOrganization(organization);
        pingPongEvent.setParticipants(new ArrayList<>());
        for (Long id:eventDto.getParticipants()) {
            pingPongEvent.getParticipants().add(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
        }
        PingPongEvent savedEvent = pingPongEventRepository.save(pingPongEvent);

        return autoMapper.mapToEventDto(savedEvent);
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
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
