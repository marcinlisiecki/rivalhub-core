package com.rivalhub.organization;

import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationNotFoundException;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryManager {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final StationRepository stationRepository;

    UserData findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
    UserData findUserById(Long id){
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    Organization save(Organization organization){
        return organizationRepository.save(organization);
    }


    Station save(Station station){
        return stationRepository.save(station);
    }

    Organization findOrganizationById(Long id){
        return organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    Station findStationById(Long id){
        return stationRepository.findById(id).orElseThrow(StationNotFoundException::new);
    }

}
