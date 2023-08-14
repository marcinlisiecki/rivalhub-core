package com.rivalhub.organization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.organization.*;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import lombok.RequiredArgsConstructor;
import com.rivalhub.user.UserData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    private final MergePatcher<OrganizationDTO> organizationMergePatcher;
    private final InvitationHelper invitationHelper;

    public OrganizationDTO saveOrganization(OrganizationDTO organizationDTO, String email){
        Organization organizationToSave = autoMapper.mapToOrganization(organizationDTO);
        Organization savedOrganization = repositoryManager.save(organizationToSave);
        UserData user = repositoryManager.findUserByEmail(email);

        UserOrganizationService.addAdminUser(user, savedOrganization);
        UserOrganizationService.addAllEventTypes(savedOrganization);

        createInvitation(savedOrganization.getId(), email);
        Organization save = repositoryManager.save(savedOrganization);

        return autoMapper.mapToOrganizationDto(save);
    }

     public OrganizationDTO findOrganization(Long id){
        return autoMapper.mapToOrganizationDto(repositoryManager.findOrganizationById(id));
    }

    public void deleteOrganization(Long id, String email) {
        OrganizationSettingsValidator.checkIfUserIsAdmin(repositoryManager.findUserByEmail(email), repositoryManager.findOrganizationById(id));
        repositoryManager.deleteOrganizationById(id);
    }

    public String createInvitation(Long id, String email) {
        UserData loggedUser = repositoryManager.findUserByEmail(email);
        Organization organization = repositoryManager.findOrganizationById(id);
        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);

        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        String hash = String.valueOf(valueToHash.hashCode() & 0x7fffffff);

        organization.setInvitationHash(hash);
        repositoryManager.save(organization);

        return invitationHelper.createInvitationLink(organization);
    }

    public void updateOrganization(Long id, JsonMergePatch patch, String email) throws JsonPatchException, JsonProcessingException {
        UserData user = repositoryManager.findUserByEmail(email);
        Organization organization = repositoryManager.findOrganizationById(id);

        OrganizationSettingsValidator.checkIfUserIsAdmin(user, organization);

        OrganizationDTO organizationDTO = findOrganization(id);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);
        patchedOrganizationDto.setId(id);

        repositoryManager.save(OrganizationMapper.map(patchedOrganizationDto, organization));
    }

}
