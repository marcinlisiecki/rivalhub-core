package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.reservation.*;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.station.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class OrganizationReservationService {
    private final AutoMapper autoMapper;
    private final ReservationRepository reservationRepository;
    private final OrganizationRepoManager organizationRepoManager;

    public ReservationDTO addReservation(AddReservationDTO addReservationDTO) {
        var organization = organizationRepoManager.getOrganizationWithStationsById(addReservationDTO.getOrganizationId());

        List<Station> stationsForReservation = getExistingStationsForReservation(organization, addReservationDTO);
        ReservationValidator.checkIfReservationIsPossible(addReservationDTO, stationsForReservation);

        var savedReservation = saveReservation(addReservationDTO, stationsForReservation);
        return autoMapper.mapToReservationDto(savedReservation);
    }

    public Reservation addReservationForEvent(AddReservationDTO addReservationDTO, Organization organization) {
        List<Station> stationsForReservation = getExistingStationsForReservation(organization, addReservationDTO);

        ReservationValidator.checkIfReservationIsPossible(addReservationDTO, stationsForReservation);

        return saveReservation(addReservationDTO, stationsForReservation);
    }

    private List<Station> getExistingStationsForReservation(Organization organization, AddReservationDTO addReservationDTO) {
        return organization.getStationList().stream()
                .filter(stationsExistsInOrganisation(addReservationDTO))
                .toList();
    }

    private Predicate<Station> stationsExistsInOrganisation(AddReservationDTO addReservationDTO) {
        return station -> addReservationDTO.getStationsIdList()
                .contains(station.getId());
    }

    private Reservation saveReservation(AddReservationDTO addReservationDTO, List<Station> reservationStations) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var newReservation = Reservation.builder()
                .userData(requestUser)
                .startTime(LocalDateTime.parse(addReservationDTO.getStartTime(), FormatterHelper.formatter()))
                .endTime(LocalDateTime.parse(addReservationDTO.getEndTime(), FormatterHelper.formatter()))
                .stationList(reservationStations)
                .build();
        return reservationRepository.save(newReservation);
    }
}
