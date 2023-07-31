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
        organization.setInvitationLink(dto.getInvitationLink());

        return organization;
    }

}
