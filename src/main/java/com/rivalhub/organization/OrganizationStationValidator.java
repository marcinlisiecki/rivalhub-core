package com.rivalhub.organization;

import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationStationValidator {
    private final OrganizationRepository organizationRepository;

    public Organization checkIfViewStationIsPossible(Long organizationId, UserData user){
        Organization organization = organizationExists(organizationId);
        return userIsInOrganization(organization, user);
    }


    private Organization organizationExists(Long id) {
        return organizationRepository.findById(id)
            .orElseThrow(OrganizationNotFoundException::new);
    }

    private Organization userIsInOrganization(Organization organization, UserData user){
        return user.getOrganizationList()
                .stream().filter(org -> org.getId().equals(organization.getId()))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }


    public void checkIfUpdateStationIsPossible(Long organizationId, UserData user) {
        Organization organization = organizationExists(organizationId);
        userIsInOrganization(organization, user);
        //TODO sprawdzanie czy user to admin
    }
}
