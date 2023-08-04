package com.rivalhub.organization;

import com.rivalhub.common.PaginationHelper;
import com.rivalhub.reservation.*;
import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.NewStationDtoMapper;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
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

        List<UserDisplayDTO> allUsers = organization.get().getUserList()
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
        String hash = String.valueOf(valueToHash.hashCode()  & 0x7fffffff);

        organization.setInvitationHash(hash);
        organizationRepository.save(organization);
        return hash;
    }

    public Optional<Organization> addUser(Long id, String hash, String email) {
        Optional<Organization> organizationRepositoryById = organizationRepository.findById(id);
        Optional<UserData> user = userRepository.findByEmail(email);

        if(user.isEmpty()) return Optional.empty();
        if (organizationRepositoryById.isEmpty()) return Optional.empty();

        Organization organization = organizationRepositoryById.get();
        if (!organization.getInvitationHash().equals(hash)) return Optional.empty();

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

    public Optional<?> addReservation(AddReservationDTO reservationDTO,
                                      Long id, String email) {
        Optional<Organization> organization = organizationRepository.findById(id);
        UserData user = userRepository.findByEmail(email).get();

        List<Station> stationList = reservationDTO.getStationsIdList().stream()
                .map(ids -> stationRepository.findById(ids))
                .map(Optional::get).toList();

        boolean checkIfReservationIsPossible = ReservationValidator.checkIfReservationIsPossible(reservationDTO, organization, user, id, stationList);
        if (!checkIfReservationIsPossible) return Optional.empty();

        ReservationSaver saver = new ReservationSaver(reservationRepository, reservationMapper);
        ReservationDTO viewReservationDTO = saver.saveReservation(user, stationList, reservationDTO);

        return Optional.of(viewReservationDTO);
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
}
