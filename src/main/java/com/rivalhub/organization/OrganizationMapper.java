package com.rivalhub.organization;

public class OrganizationMapper {
    public static Organization map(OrganizationDTO organizationDTO, Organization organization){
        organization.setName(organizationDTO.getName());
        organization.setImageUrl(organizationDTO.getImageUrl());
        organization.setColor(organizationDTO.getColor());
        return organization;
    }

    public static OrganizationDTO map(Organization organization){
        return OrganizationDTO.builder()
                .id(organization.getId())
                .name(organization.getName())
                .build();
    }

}
