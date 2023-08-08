package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.MergePatcher;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationDTOMapper organizationDTOMapper;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final MergePatcher<OrganizationDTO> organizationMergePatcher;


    // ZROBIC REFACTOR WYRZUCIC DO RESERVATION SERVICE STATION SERVICE I TAK DALEJ

    OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO, String email){
        Organization organizationToSave = organizationDTOMapper.map(organizationCreateDTO);

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        createInvitationHash(savedOrganization.getId());

        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        UserOrganizationService.addUser(user, savedOrganization);
        Organization save = organizationRepository.save(savedOrganization);

        return organizationDTOMapper.map(save);
    }

    OrganizationDTO findOrganization(Long id){
        return organizationRepository
                .findById(id)
                .map(organizationDTOMapper::map)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    void updateOrganization(OrganizationDTO organizationDTO){
        Organization organization = organizationDTOMapper.map(organizationDTO);
        organizationRepository.save(organization);
    }

    void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    String createInvitationHash(Long id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        String hash = String.valueOf(valueToHash.hashCode() & 0x7fffffff);

        organization.setInvitationHash(hash);
        organizationRepository.save(organization);
        return InvitationHelper.createInvitationLink(organizationDTOMapper.map(organization));
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

    public List<ReservationDTO> viewReservations(Long id, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        UserData user = userRepository.findByEmail(email).get();

        user.getOrganizationList().stream().filter(org -> org.getId().equals(id)).findFirst().orElseThrow(OrganizationNotFoundException::new);

        List<ReservationDTO> reservations = reservationRepository.reservationsByOrganization(organization.getId())
                .stream().map(reservationMapper::map).toList();

        return reservations;
    }

    void updateOrganization(Long id, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        OrganizationDTO organizationDTO = findOrganization(id);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);
        patchedOrganizationDto.setId(id);
        updateOrganization(patchedOrganizationDto);
    }




}
