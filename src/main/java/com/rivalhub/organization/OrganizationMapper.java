package com.rivalhub.organization;

public class OrganizationMapper {
    public static Organization map(OrganizationDTO organizationDTO, Organization organization){
        organization.setName(organizationDTO.getName());
        organization.setImageUrl(organizationDTO.getImageUrl());
        return organization;
    }
}
