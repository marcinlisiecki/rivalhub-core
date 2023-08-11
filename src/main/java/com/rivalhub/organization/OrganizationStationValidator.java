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
        return OrganizationSettingsValidator.userIsInOrganization(organization, user);
    }

    private Organization organizationExists(Long id) {
        return organizationRepository.findById(id)
            .orElseThrow(OrganizationNotFoundException::new);
    }

    public void checkIfUpdateStationIsPossible(Long organizationId, UserData user) {
        Organization organization = organizationExists(organizationId);
        OrganizationSettingsValidator.userIsInOrganization(organization, user);
        OrganizationSettingsValidator.checkIfUserIsAdmin(user, organization);
    }
}
