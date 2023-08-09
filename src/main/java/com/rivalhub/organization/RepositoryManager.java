package com.rivalhub.organization;

import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationNotFoundException;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryManager {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final StationRepository stationRepository;

    Organization findOrganization(Long id){
        return organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    UserData findUser(String email){
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }


    Organization save(Organization organization){
        return organizationRepository.save(organization);
    }


    Station save(Station station){
        return stationRepository.save(station);
    }

    Organization findOrganizationById(Long id){
        return organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
    }

    Station findStationById(Long id){
        return stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
    }

}
