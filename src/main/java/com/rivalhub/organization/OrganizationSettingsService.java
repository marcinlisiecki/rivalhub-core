package com.rivalhub.organization;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrganizationSettingsService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;

    UserDetailsDto setAdmin(String username, Long organizationId, Long userId) {
        UserData loggedUser = repositoryManager.findUserByEmail(username);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);

        UserData user = repositoryManager.findUserById(userId);

        user.getOrganizationList()
                .stream().filter(org -> org.getId().equals(organizationId))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);

        organization.getAdminUsers().add(user);
        repositoryManager.save(organization);

        return autoMapper.mapToUserDetails(user);
    }


    EventType removeEventType(String username, Long organizationId, EventType eventType) {
        UserData loggedUser = repositoryManager.findUserByEmail(username);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);
        UserOrganizationService.removeEventType(organization, eventType);
        repositoryManager.save(organization);

        return eventType;
    }

    Set<EventType> getEventTypesInOrganization(String email, Long organizationId) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData user = repositoryManager.findUserByEmail(email);
        OrganizationSettingsValidator.userIsInOrganization(organization, user);

        return organization.getEventTypeInOrganization();
    }
}
