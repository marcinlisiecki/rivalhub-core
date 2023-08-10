package com.rivalhub.organization;

import com.rivalhub.organization.exception.InsufficientPermissionsException;
import com.rivalhub.user.UserData;

public class OrganizationSettingsValidator {


    static void checkIfUserIsAdmin(UserData user, Organization organization) {
        organization.getAdminUsers()
                .stream().filter(user::equals)
                .findFirst().orElseThrow(InsufficientPermissionsException::new);
    }


}
