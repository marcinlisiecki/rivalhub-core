package com.rivalhub.organization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.station.*;
import com.rivalhub.user.UserData;
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
    private final StationRepository stationRepository;
    private final OrganizationRepository organizationRepository;
    private final AutoMapper autoMapper;
    private final MergePatcher<StationDTO> stationMergePatcher;

    public StationDTO addStation(StationDTO stationDTO, Long id) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var organizationList = requestUser.getOrganizationList();

        var organization = findOrganizationIn(organizationList, id);
        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        Station station = autoMapper.mapToStation(stationDTO);
        UserOrganizationService.addStation(station, organization);

        station = stationRepository.save(station);
        organizationRepository.save(organization);

        return autoMapper.mapToNewStationDto(station);
    }

    public List<Station> viewStations(Long organizationId, String start, String end, EventType type,
                               boolean onlyAvailable, boolean showInactive) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        if (onlyAvailable && start != null && end != null)
            return StationAvailabilityFinder
                    .getAvailableStations(organization, start, end, type, requestUser);

        if (showInactive) return StationAvailabilityFinder.filterForTypeIn(organization.getStationList(), type);
        return StationAvailabilityFinder.filterForActiveStationsAndTypeIn(organization, type);
    }

    public List<EventTypeStationsDto> getEventStations(Long organizationId, String start, String end, EventType type) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        return getEventTypeStationsByTime(start, end, type, requestUser, organization);
    }

    public void updateStation(Long organizationId, Long stationId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        StationDTO station = autoMapper.mapToNewStationDto(findStationIn(organization, stationId));
        StationDTO stationPatched = stationMergePatcher.patch(patch, station, StationDTO.class);

        stationPatched.setId(stationId);
        stationRepository.save(autoMapper.mapToStation(stationPatched));
    }

    public void deleteStation(Long stationId, Long organizationId) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        deleteStationIn(organization, stationId);
    }

    private void deleteStationIn(Organization organization, Long id){
        Station station = findStationIn(organization, id);
        organization.getStationList().remove(station);
        organizationRepository.save(organization);
    }

    private Station findStationIn(Organization organization, Long id){
        return organization.getStationList().stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private Organization findOrganizationIn(List<Organization> organizationList, Long id){
        return organizationList
                .stream().filter(org -> org.getId().equals(id))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }

    private EventTypeStationsDto getEventTypeStation(EventType eventType, List<Station> availableStations,
                                                     Organization organization, Duration timeNeeded) {
        EventTypeStationsDto eventStation = new EventTypeStationsDto();
        eventStation.setType(eventType);
        eventStation.setStations(availableStations.stream().map(autoMapper::mapToNewStationDto).toList());
        List<Station> stationList = StationAvailabilityFinder.filterForActiveStationsAndTypeIn(organization, eventType);
        eventStation.setFirstAvailable(StationAvailabilityFinder
                .getFirstDateAvailableForDuration(stationList, timeNeeded, eventType));

        return eventStation;
    }


    private List<EventTypeStationsDto> getEventTypeStationsByTime(String start, String end, EventType type, UserData requestUser, Organization organization) {
        LocalDateTime startTime = LocalDateTime.parse(start, FormatterHelper.formatter());
        LocalDateTime endTime = LocalDateTime.parse(end, FormatterHelper.formatter());

        List<EventTypeStationsDto> eventStations = new ArrayList<>();
        Duration timeNeeded = Duration.ofSeconds(ChronoUnit.SECONDS.between(startTime, endTime));

        if (type != null) {
            List<Station> availableStations = StationAvailabilityFinder
                    .getAvailableStations(organization, start, end, type, requestUser);
            eventStations.add(getEventTypeStation(type, availableStations, organization, timeNeeded));
            return eventStations;
        }

        for (EventType eventType : EventType.values()) {
            List<Station> availableStations = StationAvailabilityFinder
                    .getAvailableStations(organization, start, end, eventType, requestUser);
            eventStations.add(getEventTypeStation(eventType, availableStations, organization, timeNeeded));
        }

        return eventStations;
    }
}
