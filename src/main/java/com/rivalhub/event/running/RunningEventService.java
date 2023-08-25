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

import java.util.ArrayList;
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
    private final RunningResultsMapper runningResultsMapper;
    private final RunningEventMapper runningEventMapper;
    private final UserTimeRepository userTimeRepository;


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

    public RunningEventViewDto addRunningResults(Long eventId, List<UserTimesAddDto> userTimesAddDtos){
        RunningEvent runningEvent = runningEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        List<UserTime> userTimeList = new ArrayList<>();
        userTimesAddDtos.forEach(userTimesAddDto ->  userTimeList.add(runningResultsMapper.map(userTimesAddDto,runningEvent)));
        userTimeRepository.saveAll(userTimeList);
        runningEvent.setUserTimeList(userTimeList);
        return runningEventMapper.map(runningEventRepository.save(runningEvent));
    }

    public List<UserTimesViewDto> getRunningResults(Long eventId) {
        RunningEvent runningEvent = runningEventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        List<UserTimesViewDto> userViewTimeList = new ArrayList<>();
        runningEvent.getUserTimeList().forEach(userTime ->  userViewTimeList.add(runningResultsMapper.map(userTime,runningEvent)));
        return userViewTimeList;
    }
}
