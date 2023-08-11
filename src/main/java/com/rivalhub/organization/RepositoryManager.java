package com.rivalhub.organization;

import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.*;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationNotFoundException;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RepositoryManager {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;

    public UserData findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
    public UserData findUserById(Long id){
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public Organization save(Organization organization){
        return organizationRepository.save(organization);
    }


    public Station save(Station station){
        return stationRepository.save(station);
    }

    public Organization findOrganizationById(Long id){
        return organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public Station findStationById(Long id){
        return stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
    }

    public Reservation findReservationById(Long id){
        return reservationRepository.findById(id).orElseThrow(ReservationNotFoundException::new);
    }

    public List<Station> findStations(AddReservationDTO reservationDTO){
        return reservationDTO.getStationsIdList().stream()
                .map(stationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get).toList();
    }

    public Set<Reservation> findReservationsByOrganization(Organization organization){
        return reservationRepository.reservationsByOrganization(organization.getId());
    }

    public void deleteOrganizationById(Long id) {
        organizationRepository.deleteById(id);
    }

    public Set<Tuple> getAllUsersByOrganizationId(Long id){
        return userRepository.getAllUsersByOrganizationId(id);
    }
}
