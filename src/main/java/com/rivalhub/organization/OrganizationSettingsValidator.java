package com.rivalhub.organization;

import com.rivalhub.organization.exception.InsufficientPermissionsException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserData;
import org.aspectj.weaver.ast.Or;

public class OrganizationSettingsValidator {


    static void checkIfUserIsAdmin(UserData user, Organization organization) {
        organization.getAdminUsers()
                .stream().filter(user::equals)
                .findFirst().orElseThrow(InsufficientPermissionsException::new);
    }

    static Organization userIsInOrganization(Organization organization, UserData user){
        return user.getOrganizationList()
                .stream().filter(org -> org.getId().equals(organization.getId()))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }


}
