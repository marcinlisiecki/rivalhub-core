package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationStationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final NewStationDtoMapper newStationDtoMapper;
    private final MergePatcher<NewStationDto> stationMergePatcher;

    NewStationDto addStation(NewStationDto newStationDto, Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        List<Organization> organizationList = user.getOrganizationList();
        organizationList.stream().filter(org -> org.getId().equals(id)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        Station station = newStationDtoMapper.map(newStationDto);
        Station savedStation = stationRepository.save(station);

        UserOrganizationService.addStation(savedStation, organization);

        organizationRepository.save(organization);

        return newStationDtoMapper.map(savedStation);
    }


    List<Station> viewStations(Long id, String start, String end, EventType type, boolean onlyAvailable, UserDetails userDetails) {
        if (onlyAvailable && start != null && end != null) {
            return getAvailableStations(id, start, end, type);
        }
        return findStations(id, userDetails.getUsername());
    }

    public List<Station> getAvailableStations(long organizationId, String startTime, String endTime, EventType type) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        List<Station> allStations = organization.getStationList();
        List<Station> availableStations = new ArrayList<>();

        UserData user = userRepository
                .findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(UserNotFoundException::new);

        allStations.forEach(station -> {
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
                    organizationId,
                    List.of(station))) {

                availableStations.add(station);
            }
        });

        return availableStations;
    }

    NewStationDto findStation(Long organizationId, Long stationId, String email){
        organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        user.getOrganizationList().stream().filter(org -> org.getId().equals(organizationId)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        return stationRepository.findById(stationId).map(NewStationDtoMapper::map).orElseThrow(StationNotFoundException::new);
    }

    List<Station> findStations(Long organizationId, String email) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.getOrganizationList().stream().filter(org -> org.getId().equals(organizationId)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        return organization.getStationList();
    }

    public void updateStation(Long organizationId, Long stationId, String username, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        NewStationDto station = findStation(organizationId, stationId, username);
        NewStationDto stationPatched = stationMergePatcher.patch(patch, station, NewStationDto.class);
        stationPatched.setId(stationId);
        updateStation(stationPatched);
    }

    void updateStation(NewStationDto newStationDto){
        Station station = newStationDtoMapper.mapNewStationDtoToStation(newStationDto);
        stationRepository.save(station);
    }

    @Transactional
    void deleteStation(Long stationId, Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        UserOrganizationService.removeStation(stationRepository.findById(stationId).orElseThrow(StationNotFoundException::new), organization);
    }

}
