package com.rivalhub.event.billiards;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.*;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventService;
import com.rivalhub.event.EventType;
import com.rivalhub.event.common.EventCommonService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.profile.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BilliardsService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final BilliardsEventRepository billiardsEventRepository;
    private final BilliardsEventSaver billiardsEventSaver;
    private final EventCommonService eventCommonService;


    @Override
    public EventDto addEvent(Long organizationId, EventDto eventDto) {
        BilliardsEvent billiardsEvent = new BilliardsEvent();
        var organization  = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        return autoMapper.mapToEventDto(billiardsEventSaver.saveEvent(billiardsEvent,organization,eventDto));
    }

    @Override
    public List<EventDto> findAllEvents(long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getBilliardsEvents()
                .stream()
                .map(mapToEventDTO(organization))
                .toList();
    }

    private Function<BilliardsEvent, EventDto> mapToEventDTO(Organization organization) {
        return billiardsEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(billiardsEvent);
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(billiardsEvent, eventDto);
            return eventDto;
        };
    }

    @Override
    public EventDto findEvent(long eventId) {
        return billiardsEventRepository
                .findById(eventId)
                .map(autoMapper::mapToEventDto)
                .orElseThrow(EventNotFoundException::new);
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(billiardsEventRepository, id);
    }



    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.BILLIARDS.name());
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(billiardsEventRepository,eventId,userId);
    }

    @Override
    public List<UserDetailsDto> addUserToEvent(Long eventId, Long userId) {
        return eventCommonService.addUserToEvent(billiardsEventRepository, eventId, userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
        eventCommonService.joinPublicEvent(billiardsEventRepository, id);
    }

    @Override
    @Transactional
    public void deleteEvent(Long organizationId,Long eventId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        organization.getBilliardsEvents().remove(billiardsEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new));
        billiardsEventRepository.deleteById(eventId);
    }
}
