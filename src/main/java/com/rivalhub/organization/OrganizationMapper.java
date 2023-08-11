package com.rivalhub.organization;

public class OrganizationMapper {
    static Organization map(OrganizationDTO organizationDTO, Organization organization){
        organization.setName(organizationDTO.getName());
        organization.setInvitationHash(organizationDTO.getInvitationHash());
        organization.setImageUrl(organizationDTO.getImageUrl());

        return organization;
    }


}
