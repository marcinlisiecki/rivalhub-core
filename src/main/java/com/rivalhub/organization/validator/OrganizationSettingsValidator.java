package com.rivalhub.organization.validator;

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
}
