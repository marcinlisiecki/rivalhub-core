package com.rivalhub.organization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.organization.*;
import com.rivalhub.common.exception.FileUploadUtil;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import com.rivalhub.user.UserData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final AutoMapper autoMapper;
    private final MergePatcher<OrganizationDTO> organizationMergePatcher;
    private final FileUploadUtil fileUploadUtil;
    private final InvitationHelper invitationHelper;


    public OrganizationDTO saveOrganization(String organizationName, MultipartFile multipartFile){
        Organization organizationToSave = autoMapper.mapToOrganization(
                new OrganizationDTO(organizationName));
        var savedOrganization = organizationRepository.save(organizationToSave);

        var requestUser = SecurityUtils.getUserFromSecurityContext();

        setOrganizationSettings(requestUser, savedOrganization);
        savedOrganization.setImageUrl(fileUploadUtil.saveOrganizationImage(multipartFile, savedOrganization));

        return autoMapper.mapToOrganizationDto(organizationRepository.save(savedOrganization));
    }

     public OrganizationDTO findOrganization(Long id){
        return autoMapper.mapToOrganizationDto(organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new));
    }

    public void deleteOrganization(Long id) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        organizationRepository.delete(organization);
    }

    public String createInvitation(Long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        organization.setInvitationHash(createInvitationHash(organization));

        organizationRepository.save(organization);
        return invitationHelper.createInvitationLink(organization);
    }

    public void updateOrganization(Long id, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        OrganizationDTO organizationDTO = autoMapper.mapToOrganizationDto(organization);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);

        organizationRepository.save(OrganizationMapper.map(patchedOrganizationDto, organization));
    }

    private void setOrganizationSettings(UserData user, Organization organization){
        UserOrganizationService.addAdminUser(user, organization);
        UserOrganizationService.addAllEventTypes(organization);
        createInvitation(organization.getId());
    }

    private String createInvitationHash(Organization organization){
        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        return String.valueOf(valueToHash.hashCode() & 0x7fffffff);
    }
}
