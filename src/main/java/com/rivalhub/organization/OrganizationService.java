package com.rivalhub.organization;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.rivalhub.reservation.*;
import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.NewStationDtoMapper;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationDTOMapper organizationDTOMapper;

    private final UserRepository userRepository;

    private final NewStationDtoMapper newStationDtoMapper;

    private final StationRepository stationRepository;

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;


    public OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO, String email){
        Organization organizationToSave = organizationDTOMapper.map(organizationCreateDTO);
        organizationToSave.setAddedDate(LocalDateTime.now());

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        createInvitationLink(savedOrganization.getId());
        UserData user = userRepository.findByEmail(email).get();
        savedOrganization.addUser(user);
        Organization save = organizationRepository.save(savedOrganization);


        return organizationDTOMapper.map(save);
    }

    public Optional<OrganizationDTO> findOrganization(Long id){
        return organizationRepository.findById(id).map(organizationDTOMapper::map);
    }

    void updateOrganization(OrganizationDTO organizationDTO){
        Organization organization = organizationDTOMapper.map(organizationDTO);
        organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public String createInvitationLink(Long id) {
        Optional<Organization> organizationById = organizationRepository.findById(id);

        if (organizationById.isEmpty()) return null;

        Organization organization = organizationById.get();
        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        String hash = String.valueOf(valueToHash.hashCode()  & 0x7fffffff);

        organization.setInvitationLink(hash);
        organizationRepository.save(organization);
        return hash;
    }

    public Optional<Organization> addUser(Long id, String hash, String email) {
        Optional<Organization> organizationRepositoryById = organizationRepository.findById(id);
        Optional<UserData> user = userRepository.findByEmail(email);

        if(user.isEmpty()) return Optional.empty();
        if (organizationRepositoryById.isEmpty()) return Optional.empty();

        Organization organization = organizationRepositoryById.get();
        if (!organization.getInvitationLink().equals(hash)) return Optional.empty();

        organization.addUser(user.get());

        return Optional.of(organizationRepository.save(organization));
    }

    NewStationDto addStation(NewStationDto newStationDto, Long id, String email) {
        Optional<Organization> organizationOptional = organizationRepository.findById(id);
        if (organizationOptional.isEmpty()) return null;
        Organization organization = organizationOptional.get();

        UserData user = userRepository.findByEmail(email).get();
        List<Organization> organizationList = user.getOrganizationList();

        Organization currentOrganization = organizationList.stream().filter(org -> org.getId().equals(id)).findFirst().orElse(null);

        if (currentOrganization == null) return null;

        Station station = newStationDtoMapper.map(newStationDto);
        Station savedStation = stationRepository.save(station);
        organization.addStation(savedStation);

        organizationRepository.save(organization);

        return newStationDtoMapper.map(savedStation);
    }



    // stanowiska sa w organizacji -
    // stanowiska nie sa zarezerwowane w danym czasie -
    // user jest w organizacji +
    // stanowiska istnieja +
    // oranizajca istnieje +


    @Transactional
    public Optional<?> addReservation(AddReservationDTO reservationDTO,
                                      Long id, String email) {
        boolean b = checkIfReservationIsPossible(reservationDTO, id, email);

        if (!b) return Optional.empty();

        UserData user = userRepository.findByEmail(email).get();

        List<Station> stationList = reservationDTO.getStationsIdList().stream()
                .map(ids -> stationRepository.findById(ids))
                .map(Optional::get).toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        Reservation reservation = new Reservation(user, stationList,
                LocalDateTime.parse(reservationDTO.getStartTime(), formatter),
                LocalDateTime.parse(reservationDTO.getEndTime(), formatter));
        ReservationDTO viewReservationDTO = reservationMapper.map(reservation);

        return Optional.of(viewReservationDTO);
    }

    private boolean checkIfReservationIsPossible(AddReservationDTO reservationDTO, Long id, String email) {
        //organizacja instnieje
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isEmpty()) return false;

        // user jest w organizacji
        UserData user = userRepository.findByEmail(email).get();
        Organization userOrganization = user.getOrganizationList().stream().filter(org -> org.getId().equals(id)).findFirst().orElse(null);
        if (userOrganization == null) return false;

        //stanowiska istnieja
        List<Station> stationList;
        try{
            stationList = reservationDTO.getStationsIdList().stream()
                    .map(ids -> stationRepository.findById(ids))
                    .map(Optional::get).toList();
        }catch (NoSuchElementException noSuchElementException){
            System.out.println(noSuchElementException);
            return false;
        }catch (Exception exception){
            System.out.println(exception);
            return false;
        }

        // czy stanowiska sa w organizacji
        if (!checkIfStationsAreInOrganization(stationList, organization.get())) return false;

        // stanowiska nie sa zarezerwowane w danym czasie
        // reservationRepository.findAllByStartTimeBetweenEndAAndEndTime();

        return true;
    }

    private boolean checkIfStationsAreInOrganization(List<Station> stationList, Organization organization){
        return stationList.stream().allMatch(organization.getStationList()::contains);
    }

}
