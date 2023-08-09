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
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationStationService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final StationRepository stationRepository;
    private final AutoMapper autoMapper;
    private final MergePatcher<NewStationDto> stationMergePatcher;
    private final OrganizationStationValidator validator;

    NewStationDto addStation(NewStationDto newStationDto, Long id, String email) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);

        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        List<Organization> organizationList = user.getOrganizationList();
        organizationList
                .stream().filter(org -> org.getId().equals(id))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);

        Station station = autoMapper.mapToStation(newStationDto);
        Station savedStation = stationRepository.save(station);

        UserOrganizationService.addStation(savedStation, organization);

        organizationRepository.save(organization);

        return autoMapper.mapToNewStationDto(savedStation);
    }


    List<Station> viewStations(Long organizationId, String start, String end, EventType type,
                               boolean onlyAvailable, String email, boolean showInactive) {
        UserData user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

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

    NewStationDto findStation(Long stationId){
        return stationRepository.findById(stationId)
                .map(autoMapper::mapToNewStationDto)
                .orElseThrow(StationNotFoundException::new);
    }

    public void updateStation(Long organizationId, Long stationId, String email, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        UserData user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        validator.checkIfUpdateStationIsPossible(organizationId, user);

        NewStationDto station = findStation(stationId);
        NewStationDto stationPatched = stationMergePatcher.patch(patch, station, NewStationDto.class);
        stationPatched.setId(stationId);
        updateStation(stationPatched);
    }

    void updateStation(NewStationDto newStationDto){
        Station station = autoMapper.mapToStation(newStationDto);
        stationRepository.save(station);
    }

    @Transactional
    public void deleteStation(Long stationId, Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        UserOrganizationService.removeStation(stationRepository.findById(stationId).orElseThrow(StationNotFoundException::new), organization);
    }

    private List<Station> filterForActiveStations(List<Station> stationList){
        return stationList
                .stream().filter(Station::isActive)
                .toList();
    }

}
