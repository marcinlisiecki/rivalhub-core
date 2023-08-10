package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.ReservationValidator;
import com.rivalhub.station.*;
import com.rivalhub.user.UserData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationStationService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    private final MergePatcher<StationDTO> stationMergePatcher;
    private final OrganizationStationValidator validator;

    StationDTO addStation(StationDTO stationDTO, Long id, String email) {
        Organization organization = repositoryManager.findOrganizationById(id);

        UserData user = repositoryManager.findUserByEmail(email);
        List<Organization> organizationList = user.getOrganizationList();
        organizationList
                .stream().filter(org -> org.getId().equals(id))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);

        Station station = autoMapper.mapToStation(stationDTO);
        UserOrganizationService.addStation(station, organization);

        station = repositoryManager.save(station);

        return autoMapper.mapToNewStationDto(station);
    }


    List<Station> viewStations(Long organizationId, String start, String end, EventType type,
                               boolean onlyAvailable, String email, boolean showInactive) {
        UserData user = repositoryManager.findUserByEmail(email);

        Organization organization = validator.checkIfViewStationIsPossible(organizationId, user);

        List<Station> stationList = organization.getStationList();
        if (!showInactive) stationList = filterForActiveStations(stationList);

        if (onlyAvailable && start != null && end != null)
            return getAvailableStations(organization, start, end, type, user, stationList);

        return stationList;
    }

    public List<Station> getAvailableStations(Organization organization, String startTime,
                                              String endTime, EventType type, UserData user, List<Station> stationList) {
        List<Station> availableStations = new ArrayList<>();

        stationList.forEach(station -> {
            AddReservationDTO reservationDTO = new AddReservationDTO();
            reservationDTO.setStartTime(startTime);
            reservationDTO.setEndTime(endTime);
            reservationDTO.setStationsIdList(List.of(station.getId()));

            if (type != null && !station.getType().equals(type)) {
                return;
            }

            if (ReservationValidator.checkIfReservationIsPossible(
                    reservationDTO,
                    organization,
                    user,
                    organization.getId(),
                    List.of(station))) {

                availableStations.add(station);
            }
        });

        return availableStations;
    }

    StationDTO findStation(Long stationId){
        return autoMapper.mapToNewStationDto(repositoryManager.findStationById(stationId));
    }

    public void updateStation(Long organizationId, Long stationId, String email, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        UserData user = repositoryManager.findUserByEmail(email);

        validator.checkIfUpdateStationIsPossible(organizationId, user);

        StationDTO station = findStation(stationId);
        StationDTO stationPatched = stationMergePatcher.patch(patch, station, StationDTO.class);
        stationPatched.setId(stationId);
        updateStation(stationPatched);
    }

    void updateStation(StationDTO stationDTO){
        Station station = autoMapper.mapToStation(stationDTO);
        repositoryManager.save(station);
    }

    @Transactional
    public void deleteStation(Long stationId, Long organizationId) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserOrganizationService.removeStation(repositoryManager.findStationById(stationId), organization);
    }

    private List<Station> filterForActiveStations(List<Station> stationList){
        return stationList
                .stream().filter(Station::isActive)
                .toList();
    }

}
