package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationSettingsDTO;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationSettingsService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    private final InvitationHelper invitationHelper;

    public UserDetailsDto setAdmin(String username, Long organizationId, Long userId) {
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


    public EventType removeEventType(String username, Long organizationId, EventType eventType) {
        UserData loggedUser = repositoryManager.findUserByEmail(username);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);
        UserOrganizationService.removeEventType(organization, eventType);
        repositoryManager.save(organization);

        return eventType;
    }

    public Set<EventType> getEventTypesInOrganization(String email, Long organizationId) {
        Organization organization = repositoryManager.findOrganizationById(organizationId);
        UserData user = repositoryManager.findUserByEmail(email);
        OrganizationSettingsValidator.userIsInOrganization(organization, user);

        return organization.getEventTypeInOrganization();
    }

    public EventType addEventType(String username, Long organizationId, EventType eventType) {
        UserData loggedUser = repositoryManager.findUserByEmail(username);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);
        UserOrganizationService.addEventType(organization, eventType);
        repositoryManager.save(organization);

        return eventType;
    }

    public Set<EventType> allEventTypeInApp() {
        return Arrays.stream(EventType.values()).collect(Collectors.toSet());
    }

    public boolean setOnlyAdminCanSeeInvitationLink(String email, Long organizationId, boolean onlyAdminCanSeeInvitationLink) {
        UserData loggedUser = repositoryManager.findUserByEmail(email);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);

        organization.setOnlyAdminCanSeeInvitationLink(onlyAdminCanSeeInvitationLink);
        repositoryManager.save(organization);

        return onlyAdminCanSeeInvitationLink;
    }

    public String viewInvitationLink(Long organizationId, String email) {
        UserData loggedUser = repositoryManager.findUserByEmail(email);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        OrganizationSettingsValidator.userIsInOrganization(organization, loggedUser);

        if (!organization.getOnlyAdminCanSeeInvitationLink())
            return invitationHelper.createInvitationLink(organization);

        OrganizationSettingsValidator.checkIfUserIsAdmin(loggedUser, organization);
        return invitationHelper.createInvitationLink(organization);
    }
    public OrganizationSettingsDTO showSettings(Long id, String email) {
        UserData user = repositoryManager.findUserByEmail(email);
        Organization organization = repositoryManager.findOrganizationById(id);

        OrganizationSettingsValidator.userIsInOrganization(organization, user);

        return new OrganizationSettingsDTO(organization.getOnlyAdminCanSeeInvitationLink());
    }
}
