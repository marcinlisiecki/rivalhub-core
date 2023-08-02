package com.rivalhub.organization;

import org.springframework.stereotype.Service;

@Service
public class OrganizationDTOMapper {
    OrganizationDTO map(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();

        dto.setName(organization.getName());
        dto.setId(organization.getId());
        dto.setImageUrl(organization.getImageUrl());
        dto.setInvitationLink(organization.getInvitationLink());

        return dto;
    }

    Organization map(OrganizationDTO dto){
        Organization organization = new Organization();

        organization.setName(dto.getName());
        organization.setId(dto.getId());
        organization.setImageUrl(dto.getImageUrl());

        return organization;
    }

    Organization map(OrganizationCreateDTO organizationCreateDTO){
        Organization organization = new Organization();

        organization.setName(organizationCreateDTO.getName());
        organization.setId(organizationCreateDTO.getId());
        organization.setImageUrl(organizationCreateDTO.getImageUrl());

        return organization;
    }

    OrganizationCreateDTO mapToOrganizationCreateDTO(Organization organization){
        OrganizationCreateDTO organizationCreateDTO = new OrganizationCreateDTO();

        organizationCreateDTO.setId(organization.getId());
        organizationCreateDTO.setName(organization.getName());
        organizationCreateDTO.setImageUrl(organization.getImageUrl());

        return organizationCreateDTO;
    }

}
