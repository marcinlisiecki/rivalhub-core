package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.OrganizationSettingsDTO;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.common.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationSettingsService {
    private final OrganizationRepository organizationRepository;
    private final AutoMapper autoMapper;
    private final InvitationHelper invitationHelper;

    public UserDetailsDto setAdmin(Long organizationId, Long userId) {
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        UserData user = findUserInOrganization(organization, userId);
        organization.getAdminUsers().add(user);

        organizationRepository.save(organization);
        return autoMapper.mapToUserDetails(user);
    }

    public void removeAdmin(Long organizationId, Long userId) {
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        UserData user = findUserInOrganization(organization, userId);
        organization.getAdminUsers().remove(user);
        organizationRepository.save(organization);
    }


    public void removeEventType(Long organizationId, List<EventType> eventTypes) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        UserOrganizationService.removeEventType(organization, eventTypes);

        organizationRepository.save(organization);
    }

    public Set<EventType> getEventTypesInOrganization(Long organizationId) {
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);
        return organization.getEventTypeInOrganization();
    }

    public List<EventType> addEventType(Long organizationId, List<EventType> eventTypes) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        UserOrganizationService.addEventType(organization, eventTypes);

        organizationRepository.save(organization);
        return eventTypes;
    }

    public Set<EventType> allEventTypeInApp() {
        return Arrays.stream(EventType.values()).collect(Collectors.toSet());
    }

    public boolean setOnlyAdminCanSeeInvitationLink(Long organizationId, boolean onlyAdminCanSeeInvitationLink) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        organization.setOnlyAdminCanSeeInvitationLink(onlyAdminCanSeeInvitationLink);

        organizationRepository.save(organization);
        return onlyAdminCanSeeInvitationLink;
    }

    public String viewInvitationLink(Long organizationId) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        final var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        return getInvitation(organization, requestUser);
    }
    public OrganizationSettingsDTO showSettings(Long id) {
        final var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        return new OrganizationSettingsDTO(organization.getOnlyAdminCanSeeInvitationLink());
    }

    private UserData findUserInOrganization(Organization organization, Long id){
        return organization.getUserList()
                .stream().filter(userData -> userData.getId().equals(id))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    private String getInvitation(Organization organization, UserData requestUser){
        if (!organization.getOnlyAdminCanSeeInvitationLink())
            return invitationHelper.createInvitationLink(organization);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        return invitationHelper.createInvitationLink(organization);
    }
}
