package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.exception.ReservationIsNotPossible;
import com.rivalhub.reservation.*;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrganizationReservationService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    private final ReservationSaver reservationSaver;


    public ReservationDTO addReservation(AddReservationDTO reservationDTO,
                                         Long id, String email) {
        Organization organization = repositoryManager.findOrganizationById(id);
        UserData user = repositoryManager.findUserByEmail(email);

        List<Station> stationList = repositoryManager.findStations(reservationDTO);

        boolean checkIfReservationIsPossible = ReservationValidator.checkIfReservationIsPossible(reservationDTO, organization, user, id, stationList);
        if (!checkIfReservationIsPossible) throw new ReservationIsNotPossible();

        return reservationSaver.saveReservation(user, stationList, reservationDTO);
    }

    public List<ReservationDTO> viewReservations(Long id, String email) {
        Organization organization = repositoryManager.findOrganizationById(id);
        UserData user = repositoryManager.findUserByEmail(email);

        user.getOrganizationList()
                .stream().filter(org -> org.getId().equals(id)).findFirst()
                .orElseThrow(OrganizationNotFoundException::new);

        return repositoryManager.findReservationsByOrganization(organization)
                .stream().map(autoMapper::mapToReservationDto).toList();
    }
}
