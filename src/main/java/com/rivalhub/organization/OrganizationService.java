package com.rivalhub.organization;

import com.rivalhub.reservation.*;
import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.NewStationDtoMapper;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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


    public Optional<?> addReservation(AddReservationDTO reservationDTO,
                                      Long id, String email) {
        boolean checkIfReservationIsPossible = checkIfReservationIsPossible(reservationDTO, id, email);

        if (!checkIfReservationIsPossible) return Optional.empty();

        UserData user = userRepository.findByEmail(email).get();

        List<Station> stationList = reservationDTO.getStationsIdList().stream()
                .map(ids -> stationRepository.findById(ids))
                .map(Optional::get).toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        Reservation reservation = new Reservation(user, stationList,
                LocalDateTime.parse(reservationDTO.getStartTime(), formatter),
                LocalDateTime.parse(reservationDTO.getEndTime(), formatter));
        ReservationDTO viewReservationDTO = reservationMapper.map(reservation);

        stationList.stream().forEach(station -> station.addReservation(reservation));


        List<List<LocalDateTime>> listsOfStartTime = stationList.stream().map(station
                -> station.getReservationList().stream().map(reservations -> reservations.getStartTime()).toList()).toList();
        List<List<LocalDateTime>> listsOfEndTime = stationList.stream().map(station
                -> station.getReservationList().stream().map(reservations -> reservations.getEndTime()).toList()).toList();

        List<LocalDateTime> listOfStartTime =
                listsOfStartTime.stream()
                        .flatMap(List::stream).toList();

        List<LocalDateTime> listOfEndTime =
                listsOfEndTime.stream()
                        .flatMap(List::stream).toList();


        List<Instant> instantsStartTime = listOfStartTime.stream().map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant()).toList();
        List<Instant> instantsEndTime = listOfEndTime.stream().map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant()).toList();

        instantsStartTime.forEach(t -> System.out.println(t.toEpochMilli()));
        instantsEndTime.forEach(t -> System.out.println(t.toEpochMilli()));


        long reservationStartTime =  reservation.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long reservationEndTime =  reservation.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        for(int i = 0; i < instantsStartTime.size(); i++){
            long timeStart = instantsStartTime.get(i).toEpochMilli();
            long timeEnd = instantsEndTime.get(i).toEpochMilli();

            long timeStartReservationStartControl = timeStart - reservationStartTime;
            long timeStartReservationEndControl = timeStart - reservationEndTime;

            long timeEndReservationStartControl = timeEnd - reservationStartTime;
            long timeEndReservationEndControl = timeEnd - reservationEndTime;

            if (timeStartReservationEndControl * timeStartReservationStartControl < 0) return Optional.empty();
            if (timeEndReservationStartControl * timeEndReservationEndControl < 0) return Optional.empty();
        }

        reservationRepository.save(reservation);
        return Optional.of(viewReservationDTO);
    }

    private boolean checkIfReservationIsPossible(AddReservationDTO reservationDTO, Long id, String email) {
        // organizacja instnieje
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isEmpty()) return false;

        // user jest w organizacji
        UserData user = userRepository.findByEmail(email).get();
        Organization userOrganization = user.getOrganizationList().stream().filter(org -> org.getId().equals(id)).findFirst().orElse(null);
        if (userOrganization == null) return false;

        // stanowiska istnieja
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
        // reservationRepository;

        return true;
    }

    private boolean checkIfStationsAreInOrganization(List<Station> stationList, Organization organization){
        return stationList.stream().allMatch(organization.getStationList()::contains);
    }

    public Optional<List<Station>> findStations(Long organizationId) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization.isEmpty()) return Optional.empty();

        List<Station> stationList = organization.get().getStationList();
        return Optional.of(stationList);
    }

    public Optional<NewStationDto> findStation(Long stationId){
        return stationRepository.findById(stationId).map(newStationDtoMapper::map);
    }

    void updateStation(NewStationDto newStationDto){
        Station station = newStationDtoMapper.map(newStationDto);
        stationRepository.save(station);
    }

    @Transactional
    public void deleteStation(Long stationId, Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId).get();
        organization.removeStation(stationRepository.findById(stationId).get());
    }
}
