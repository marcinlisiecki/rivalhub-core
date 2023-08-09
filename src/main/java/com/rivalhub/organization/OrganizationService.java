package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
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
    private final AutoMapper autoMapper;
    private final UserRepository userRepository;
    private final MergePatcher<OrganizationDTO> organizationMergePatcher;

    OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO, String email){
        Organization organizationToSave = autoMapper.mapToOrganization(organizationCreateDTO);

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        createInvitationHash(savedOrganization.getId());

        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        UserOrganizationService.addUser(user, savedOrganization);
        Organization save = organizationRepository.save(savedOrganization);

        return autoMapper.mapToOrganizationDto(save);
    }

    OrganizationDTO findOrganization(Long id){
        return organizationRepository
                .findById(id)
                .map(autoMapper::mapToOrganizationDto)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    void updateOrganization(OrganizationDTO organizationDTO){
        Organization organization = autoMapper.mapToOrganization(organizationDTO);
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
        return InvitationHelper.createInvitationLink(autoMapper.mapToOrganizationDto(organization));
    }

    void updateOrganization(Long id, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        OrganizationDTO organizationDTO = findOrganization(id);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);
        patchedOrganizationDto.setId(id);
        updateOrganization(patchedOrganizationDto);
    }
}
