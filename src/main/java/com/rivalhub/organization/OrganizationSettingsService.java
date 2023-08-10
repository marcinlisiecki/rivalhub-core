package com.rivalhub.organization;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.exception.InsufficientPermissionsException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationSettingsService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;

    public UserDetailsDto setAdmin(String username, Long organizationId, Long userId) {
        UserData loggedUser = repositoryManager.findUserByEmail(username);
        Organization organization = repositoryManager.findOrganizationById(organizationId);

        organization.getAdminUsers()
                .stream().filter(user -> user.equals(loggedUser))
                .findFirst().orElseThrow(InsufficientPermissionsException::new);

        UserData user = repositoryManager.findUserById(userId);

        user.getOrganizationList()
                .stream().filter(org -> org.getId().equals(organizationId))
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);

        organization.getAdminUsers().add(user);
        repositoryManager.save(organization);

        return autoMapper.mapToUserDetails(user);
    }


}
