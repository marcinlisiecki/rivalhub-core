package com.rivalhub.organization;

import com.rivalhub.common.PaginationHelper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.exception.ReservationIsNotPossible;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.event.EventType;
import com.rivalhub.reservation.*;
import com.rivalhub.station.*;
import com.rivalhub.user.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    private final UserDtoMapper userDtoMapper;

    private final EmailService emailService;

    public OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO, String email){
        Organization organizationToSave = organizationDTOMapper.map(organizationCreateDTO);
        organizationToSave.setAddedDate(LocalDateTime.now());

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        createInvitationHash(savedOrganization.getId());

        Optional<UserData> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        savedOrganization.addUser(user.get());
        Organization save = organizationRepository.save(savedOrganization);


        return organizationDTOMapper.map(save);
    }

    public OrganizationDTO findOrganization(Long id){
        return organizationRepository
                .findById(id)
                .map(organizationDTOMapper::map)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public Page<?> findUsersByOrganization(Long id, int page, int size){
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isEmpty()) return Page.empty();

        List<UserDetailsDto> allUsers = organization.get().getUserList()
                .stream().map(userDtoMapper::mapToUserDisplayDTO).toList();

        return PaginationHelper.toPage(page, size, allUsers);
    }

    void updateOrganization(OrganizationDTO organizationDTO){
        Organization organization = organizationDTOMapper.map(organizationDTO);
        organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public String createInvitationHash(Long id) {
        Optional<Organization> organizationById = organizationRepository.findById(id);

        if (organizationById.isEmpty()) return null;

        Organization organization = organizationById.get();
        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        String hash = String.valueOf(valueToHash.hashCode() & 0x7fffffff);

        organization.setInvitationHash(hash);
        organizationRepository.save(organization);
        return hash;
    }

    public Organization addUser(Long id, String hash, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).get();

        if (!organization.getInvitationHash().equals(hash)) throw new WrongInvitationException();
        if (user.getOrganizationList().contains(organization)) throw new AlreadyInOrganizationException();

        organization.addUser(user);

        return organizationRepository.save(organization);
    }

    NewStationDto addStation(NewStationDto newStationDto, Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        UserData user = userRepository.findByEmail(email).get();
        List<Organization> organizationList = user.getOrganizationList();
        organizationList.stream().filter(org -> org.getId().equals(id)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        Station station = newStationDtoMapper.map(newStationDto);
        Station savedStation = stationRepository.save(station);

        organization.addStation(savedStation);
        organizationRepository.save(organization);

        return newStationDtoMapper.map(savedStation);
    }

    public ReservationDTO addReservation(AddReservationDTO reservationDTO,
                                      Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).get();

        List<Station> stationList = reservationDTO.getStationsIdList().stream()
                .map(ids -> stationRepository.findById(ids))
                .map(Optional::get).toList();


        boolean checkIfReservationIsPossible = ReservationValidator.checkIfReservationIsPossible(reservationDTO, organization, user, id, stationList);
        if (!checkIfReservationIsPossible) throw new ReservationIsNotPossible();

        ReservationSaver saver = new ReservationSaver(reservationRepository, reservationMapper);
        ReservationDTO viewReservationDTO = saver.saveReservation(user, stationList, reservationDTO);

        return viewReservationDTO;
    }


    public List<Station> findStations(Long organizationId, String email) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);

        UserData user = userRepository.findByEmail(email).get();
        user.getOrganizationList().stream().filter(org -> org.getId().equals(organizationId)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        return organization.getStationList();
    }

    public NewStationDto findStation(Long organizationId, Long stationId, String email){
        organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).get();

        user.getOrganizationList().stream().filter(org -> org.getId().equals(organizationId)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        return stationRepository.findById(stationId).map(newStationDtoMapper::map).orElseThrow(StationNotFoundException::new);
    }

    void updateStation(NewStationDto newStationDto){
        Station station = newStationDtoMapper.mapNewStationDtoToStation(newStationDto);
        stationRepository.save(station);
    }

    @Transactional
    public void deleteStation(Long stationId, Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(OrganizationNotFoundException::new);
        organization.removeStation(stationRepository.findById(stationId).orElseThrow(StationNotFoundException::new));
    }

    public String createInvitationLink(OrganizationDTO organizationDTO){
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
        uri.replacePath("");
        builder.append("Enter the link to join: \n")
                .append(uri.toUriString()).append("/")
                .append(organizationDTO.getId())
                .append("/invitation/")
                .append(organizationDTO.getInvitationHash());
        String body = builder.toString();
        return body;
    }

    public OrganizationDTO addUserThroughEmail(Long id, String email) {
        OrganizationDTO organizationDTO = findOrganization(id);

        String subject = "Invitation to " + organizationDTO.getName();
        String body = createInvitationLink(organizationDTO);
        emailService.sendSimpleMessage(email, subject, body);

        return organizationDTO;
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

    public List<ReservationDTO> viewReservations(Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).get();

        user.getOrganizationList().stream().filter(org -> org.getId().equals(id)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        List<ReservationDTO> reservations = reservationRepository.reservationsByOrganization(organization.getId())
                .stream().map(reservationMapper::map).toList();

        return reservations;
    }
}
