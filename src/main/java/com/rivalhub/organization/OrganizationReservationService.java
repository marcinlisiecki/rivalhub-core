package com.rivalhub.organization;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.exception.ReservationIsNotPossible;
import com.rivalhub.reservation.*;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrganizationReservationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;
    private final AutoMapper autoMapper;
    private final ReservationSaver reservationSaver;


    public ReservationDTO addReservation(AddReservationDTO reservationDTO,
                                         Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        List<Station> stationList = reservationDTO.getStationsIdList().stream()
                .map(stationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get).toList();

        boolean checkIfReservationIsPossible = ReservationValidator.checkIfReservationIsPossible(reservationDTO, organization, user, id, stationList);
        if (!checkIfReservationIsPossible) throw new ReservationIsNotPossible();

        return reservationSaver.saveReservation(user, stationList, reservationDTO);
    }

    List<ReservationDTO> viewReservations(Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        user.getOrganizationList().stream().filter(org -> org.getId().equals(id)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        return reservationRepository.reservationsByOrganization(organization.getId())
                .stream().map(autoMapper::mapToReservationDto).toList();
    }
}
