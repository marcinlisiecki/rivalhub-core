package com.rivalhub.organization.validator;

import com.rivalhub.common.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.Organization;
import com.rivalhub.common.exception.InsufficientPermissionsException;
import com.rivalhub.user.UserData;

public class OrganizationSettingsValidator {


    public static void checkIfUserIsAdmin(UserData user, Organization organization) {
        organization.getAdminUsers()
                .stream().filter(user::equals)
                .findFirst()
                .orElseThrow(InsufficientPermissionsException::new);
    }

    public static void throwIfUserIsInOrganization(Organization organization, UserData userToAdd) {
        if(organization.getUserList().stream().anyMatch(userToAdd::equals)) throw new ImpossibleToAddUser();
    }
}
