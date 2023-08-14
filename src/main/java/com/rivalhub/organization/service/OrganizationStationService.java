package com.rivalhub.organization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.organization.validator.OrganizationStationValidator;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.station.EventTypeStationsDto;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationAvailabilityFinder;
import com.rivalhub.station.StationDTO;
import com.rivalhub.user.UserData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationStationService {

    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    private final MergePatcher<StationDTO> stationMergePatcher;
    private final OrganizationStationValidator validator;

    public StationDTO addStation(StationDTO stationDTO, Long id, String email) {
        UserData user = repositoryManager.findUserByEmail(email);
        List<Organization> organizationList = user.getOrganizationList();

        Organization organization = organizationList
                .stream().filter(org -> org.getId().equals(id))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(user, organization);

        Station station = autoMapper.mapToStation(stationDTO);
        UserOrganizationService.addStation(station, organization);

        station = repositoryManager.save(station);

        return autoMapper.mapToNewStationDto(station);
    }


    public List<Station> viewStations(Long organizationId, String start, String end, EventType type,
                               boolean onlyAvailable, String email, boolean showInactive) {
        UserData user = repositoryManager.findUserByEmail(email);

        Organization organization = validator.checkIfViewStationIsPossible(organizationId, user);

        List<Station> stationList = organization.getStationList();
        if (!showInactive) stationList = filterForActiveStations(stationList);

        if (onlyAvailable && start != null && end != null)
            return StationAvailabilityFinder
                    .getAvailableStations(organization, start, end, type, user, stationList);

        return stationList;
    }

    public List<EventTypeStationsDto> getEventStations(Long organizationId, String start, String end, EventType type) {
        UserData userData = repositoryManager
                .findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        Organization organization = repositoryManager.findOrganizationById(organizationId);


        LocalDateTime startTime = LocalDateTime.parse(start, FormatterHelper.formatter());
        LocalDateTime endTime = LocalDateTime.parse(end, FormatterHelper.formatter());

        List<EventTypeStationsDto> eventStations = new ArrayList<>();
        Duration timeNeeded = Duration.ofSeconds(ChronoUnit.SECONDS.between(startTime, endTime));

        if (type != null) {
            List<Station> availableStations = StationAvailabilityFinder
                    .getAvailableStations(organization, start, end, type, userData, organization.getStationList());
            availableStations = filterForActiveStations(availableStations);
            eventStations.add(getEventTypeStation(type, availableStations, organization, timeNeeded));
            return eventStations;
        }

        for (EventType eventType : EventType.values()) {
            List<Station> availableStations = StationAvailabilityFinder
                    .getAvailableStations(organization, start, end, eventType, userData, organization.getStationList());
            availableStations = filterForActiveStations(availableStations);
            eventStations.add(getEventTypeStation(eventType, availableStations, organization, timeNeeded));
        }

        return eventStations;
    }

    private EventTypeStationsDto getEventTypeStation(EventType eventType,
                                                     List<Station> availableStations,
                                                     Organization organization,
                                                     Duration timeNeeded) {

        EventTypeStationsDto eventStation = new EventTypeStationsDto();
        eventStation.setType(eventType);
        eventStation.setStations(availableStations.stream().map(autoMapper::mapToNewStationDto).toList());
        eventStation.setFirstAvailable(StationAvailabilityFinder
                .getFirstDateAvailableForDuration(organization.getStationList(), timeNeeded, eventType));

        return eventStation;
    }

    StationDTO findStation(Long stationId) {
        return autoMapper.mapToNewStationDto(repositoryManager.findStationById(stationId));
    }

    public void updateStation(Long organizationId, Long stationId, String email, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        UserData user = repositoryManager.findUserByEmail(email);

        validator.checkIfUpdateStationIsPossible(organizationId, user);

        StationDTO station = findStation(stationId);
        StationDTO stationPatched = stationMergePatcher.patch(patch, station, StationDTO.class);
        stationPatched.setId(stationId);

        repositoryManager.save(autoMapper.mapToStation(stationPatched));
    }

    @Transactional
    public void deleteStation(Long stationId, Long organizationId, String email) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        OrganizationSettingsValidator.checkIfUserIsAdmin(repositoryManager.findUserByEmail(email), organization);
        UserOrganizationService.removeStation(repositoryManager.findStationById(stationId), organization);
    }

    private List<Station> filterForActiveStations(List<Station> stationList) {
        return stationList
                .stream().filter(Station::isActive)
                .toList();
    }

}
