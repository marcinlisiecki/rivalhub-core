package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.*;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class OrganizationReservationService {
    private final AutoMapper autoMapper;
    //OrganizationReservationService ma zależności od organization repo i reservation repo (jak nazwa wskazuje) czy to nie piękne?
    private final OrganizationRepository organizationRepository;
    private final ReservationRepository reservationRepository;


    public ReservationDTO addReservation(AddReservationDTO addReservationDTO) {
        final var organization = organizationRepository.findById(addReservationDTO.getOrganizationId())
                .orElseThrow(OrganizationNotFoundException::new);

        List<Station> stationsForReservation = getExistingStationsForReservation(organization, addReservationDTO);

        ReservationValidator.checkIfReservationIsPossible(addReservationDTO, stationsForReservation);

        var savedReservation = saveReservation(addReservationDTO, stationsForReservation);
        return autoMapper.mapToReservationDto(savedReservation);
    }

    public Reservation addReservationForEvent(AddReservationDTO addReservationDTO, Organization organization) {
        List<Station> stationsForReservation = getExistingStationsForReservation(organization, addReservationDTO);

        ReservationValidator.checkIfReservationIsPossible(addReservationDTO, stationsForReservation);

        var savedReservation = saveReservation(addReservationDTO, stationsForReservation);
        return savedReservation;
    }

    private List<Station> getExistingStationsForReservation(Organization organization, AddReservationDTO addReservationDTO) {
        return organization.getStationList().stream()
                .filter(stationsExistsInOrganisation(addReservationDTO))
                .toList();
    }

    private Predicate<Station> stationsExistsInOrganisation(AddReservationDTO addReservationDTO) {
        return station -> addReservationDTO.getStationsIdList().contains(station.getId());
    }

    private Reservation saveReservation(AddReservationDTO addReservationDTO, List<Station> reservationStations) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var newReservation = Reservation.builder()
                .userData(requestUser)
                .startTime(LocalDateTime.parse(addReservationDTO.getStartTime(), FormatterHelper.formatter()))
                .endTime(LocalDateTime.parse(addReservationDTO.getEndTime(), FormatterHelper.formatter()))
                .stationList(reservationStations)
                .build();
        return reservationRepository.save(newReservation);
    }


    /*
    Tutaj była logika sprwadzania czy user jest w danej organizacji (chyba) to generuje dużo zbędnego kodu w róznych miejscach.
    Podejście
        a) ufamy, że skoro user przeszedł autentykacje to może wykonać dane działanie
        b) robimy jakąś klasę która wystawia metodę sprawdzania tego czy dany user jest w organizacji i wołamy ją w różnych miejscach
        c) robimy własną anotację i piszemy do niej metodę która będzie to sprawdzać, po czym oznaczamy anotacją wybrane metody
     */
    public List<ReservationDTO> viewReservations(Long organizationId) {
        return reservationRepository.reservationsByOrganization(organizationId)
                .stream().map(autoMapper::mapToReservationDto).toList();
    }


}
