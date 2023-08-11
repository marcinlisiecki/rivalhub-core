package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.PaginationHelper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationUserService {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    private final EmailService emailService;
    private final InvitationHelper invitationHelper;

    public Page<?> findUsersByOrganization(Long id, int page, int size) {
        Organization organization = repositoryManager.findOrganizationById(id);

        List<UserDetailsDto> allUsers = organization.getUserList()
                .stream().map(autoMapper::mapToUserDisplayDTO).toList();
        return PaginationHelper.toPage(page, size, allUsers);
    }

    public OrganizationDTO addUser(Long id, String hash, String email) {
        Organization organization = repositoryManager.findOrganizationById(id);
        if (!organization.getInvitationHash().equals(hash)) throw new WrongInvitationException();

        UserData user = checkIfUserIsInOrganization(repositoryManager.findUserByEmail(email), organization);

        UserOrganizationService.addUser(user, organization);
        Organization save = repositoryManager.save(organization);

        return autoMapper.mapToOrganizationDto(save);
    }

    private UserData checkIfUserIsInOrganization(UserData user, Organization organization) {
        if (user.getOrganizationList().contains(organization)) throw new AlreadyInOrganizationException();
        return user;
    }

    public OrganizationDTO addUserThroughEmail(Long id, String email) {
        Organization organization = repositoryManager.findOrganizationById(id);
        String subject = "Invitation to " + organization.getName();

        String body = invitationHelper.createInvitationLink(organization);
        emailService.sendSimpleMessage(email, subject, body);

        return autoMapper.mapToOrganizationDto(organization);
    }

    public Set<UserDetailsDto> viewAllUsers(Long id, String email) {
        UserData user = repositoryManager.findUserByEmail(email);
        Organization organization = repositoryManager.findOrganizationById(id);

        OrganizationSettingsValidator.userIsInOrganization(organization, user);

        return repositoryManager.getAllUsersByOrganizationId(id)
                .stream().map(u -> new UserDetailsDto(u.get(0, Long.class),
                                                      u.get(1, String.class),
                                                      u.get(2, String.class),
                                                      u.get(3, String.class),
                                                      u.get(4, LocalDateTime.class)))
                .collect(Collectors.toSet());
    }
}
