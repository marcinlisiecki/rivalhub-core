package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final AutoMapper autoMapper;
    private final UserRepository userRepository;
    private final MergePatcher<OrganizationDTO> organizationMergePatcher;
    private final InvitationHelper invitationHelper;

    OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO, String email){
        Organization organizationToSave = autoMapper.mapToOrganization(organizationCreateDTO);

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        UserOrganizationService.addAdminUser(user, savedOrganization);
        UserOrganizationService.addAllEventTypes(savedOrganization);

        createInvitationHash(savedOrganization.getId(), user);


        Organization save = organizationRepository.save(savedOrganization);

        return autoMapper.mapToOrganizationDto(save);
    }

    OrganizationDTO findOrganization(Long id){
        return organizationRepository
                .findById(id)
                .map(autoMapper::mapToOrganizationDto)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    //TODO tylko admin może widzieć link chyba że jest inaczej w ustawieniach organizacji
    String createInvitationHash(Long id, UserData loggedUser) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);

        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        String hash = String.valueOf(valueToHash.hashCode() & 0x7fffffff);

        organization.setInvitationHash(hash);
        organizationRepository.save(organization);
        return invitationHelper.createInvitationLink(autoMapper.mapToOrganizationDto(organization));
    }

    void updateOrganization(Long id, JsonMergePatch patch, String email) throws JsonPatchException, JsonProcessingException {
        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(user, organization);

        OrganizationDTO organizationDTO = findOrganization(id);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);
        patchedOrganizationDto.setId(id);

        organizationRepository.save(OrganizationMapper.map(patchedOrganizationDto, organization));
    }

    String createInvitation(Long id, String email) {
        UserData loggedUser = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return createInvitationHash(id, loggedUser);
    }
}
