package com.rivalhub.organization.validator;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.exception.InsufficientPermissionsException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserData;

public class OrganizationSettingsValidator {


    public static void checkIfUserIsAdmin(UserData user, Organization organization) {
        organization.getAdminUsers()
                .stream().filter(user::equals)
                .findFirst().orElseThrow(InsufficientPermissionsException::new);
    }

    public static Organization userIsInOrganization(Organization organization, UserData user){
        return user.getOrganizationList()
                .stream().filter(org -> org.getId().equals(organization.getId()))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }


}
