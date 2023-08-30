package com.rivalhub.event.running;

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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunningEventService implements EventService {

    private final AutoMapper autoMapper;
    private final OrganizationRepository organizationRepository;
    private final RunningEventRepository runningEventRepository;
    private final RunningEventSaver runningEventSaver;
    private final EventCommonService eventCommonService;
    private final RunningResultsMapper runningResultsMapper;
    private final RunningResultSaver runningResultSaver;


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
                .map(mapToEventDTO(organization))
                .toList();
    }

    private Function<RunningEvent, EventDto> mapToEventDTO(Organization organization) {
        return runningEvent -> {
            EventDto eventDto = autoMapper.mapToEventDto(runningEvent);
            eventDto.setIsEventPublic(runningEvent.isEventPublic());
            eventDto.setOrganization(autoMapper.mapToOrganizationDto(organization));

            eventCommonService.setStatusForEvent(runningEvent, eventDto);
            return eventDto;
        };
    }

    @Override
    public EventDto findEvent(long eventId) {
        RunningEvent event = runningEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventDto eventDto = autoMapper.mapToEventDto(event);
        eventDto.setIsEventPublic(event.isEventPublic());

        return eventDto;
    }

    @Override
    public List<UserDetailsDto> findAllParticipants(long id) {
        return eventCommonService.findAllParticipants(runningEventRepository, id);
    }



    @Override
    public boolean matchStrategy(String eventType) {
        return eventType.equalsIgnoreCase(EventType.RUNNING.name());
    }

    public RunningEventViewDto addRunningResults(Long eventId, List<UserTimesAddDto> userTimesAddDtos){
        return runningResultSaver.save(eventId,userTimesAddDtos);
    }

    public List<UserTimesViewDto> getRunningResults(Long eventId) {
        RunningEvent runningEvent = runningEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        List<UserTimesViewDto> userViewTimeList = new ArrayList<>();
        runningEvent.getUserTimeList().forEach(userTime ->  userViewTimeList.add(runningResultsMapper.map(userTime,runningEvent)));
        return userViewTimeList;
    }

    @Override
    public List<UserDetailsDto> deleteUserFromEvent(Long eventId, Long userId) {
        return eventCommonService.deleteUserFromEvent(runningEventRepository,eventId,userId);
    }

    @Override
    public List<UserDetailsDto> addUserToEvent(Long eventId, Long userId) {
        return eventCommonService.addUserToEvent(runningEventRepository, eventId, userId);
    }

    @Override
    public void joinPublicEvent(Long id) {
        eventCommonService.joinPublicEvent(runningEventRepository, id);
    }

    @Override
    @Transactional
    public void deleteEvent(Long organizationId,Long eventId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        organization.getRunningEvents().remove(runningEventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new));

        runningEventRepository.deleteById(eventId);
    }
}
