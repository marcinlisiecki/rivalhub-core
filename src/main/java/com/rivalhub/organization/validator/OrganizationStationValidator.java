package com.rivalhub.organization.validator;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationStationValidator {
    private final RepositoryManager repositoryManager;

    public Organization checkIfViewStationIsPossible(Long organizationId, UserData user){
        Organization organization = organizationExists(organizationId);
        return OrganizationSettingsValidator.userIsInOrganization(organization, user);
    }

    private Organization organizationExists(Long id) {
        return repositoryManager.findOrganizationById(id);
    }

    public void checkIfUpdateStationIsPossible(Long organizationId, UserData user) {
        Organization organization = organizationExists(organizationId);
        OrganizationSettingsValidator.userIsInOrganization(organization, user);
        OrganizationSettingsValidator.checkIfUserIsAdmin(user, organization);
    }
}
