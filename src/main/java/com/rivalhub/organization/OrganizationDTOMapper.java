package com.rivalhub.organization;

import com.rivalhub.organization.exception.OrganizationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationDTOMapper {

    private final OrganizationRepository organizationRepository;

    OrganizationDTO map(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();

        dto.setName(organization.getName());
        dto.setId(organization.getId());
        dto.setImageUrl(organization.getImageUrl());
        dto.setInvitationHash(organization.getInvitationHash());

        return dto;
    }

    Organization map(OrganizationDTO dto) {
        Organization organization = new Organization();

        organization.setName(dto.getName());
        organization.setId(dto.getId());
        organization.setImageUrl(dto.getImageUrl());
        organization.setInvitationHash(dto.getInvitationHash());

        organization.setStationList(organizationRepository
                .findById(dto.getId())
                .orElseThrow(OrganizationNotFoundException::new)
                .getStationList());

        organization.setUserList(organizationRepository
                .findById(dto.getId())
                .orElseThrow(OrganizationNotFoundException::new)
                .getUserList());

        return organization;
    }

    Organization map(OrganizationCreateDTO organizationCreateDTO) {
        Organization organization = new Organization();

        organization.setName(organizationCreateDTO.getName());
        organization.setId(organizationCreateDTO.getId());
        organization.setImageUrl(organizationCreateDTO.getImageUrl());

        return organization;
    }

    OrganizationCreateDTO mapToOrganizationCreateDTO(Organization organization) {
        OrganizationCreateDTO organizationCreateDTO = new OrganizationCreateDTO();

        organizationCreateDTO.setId(organization.getId());
        organizationCreateDTO.setName(organization.getName());
        organizationCreateDTO.setImageUrl(organization.getImageUrl());

        return organizationCreateDTO;
    }

}
