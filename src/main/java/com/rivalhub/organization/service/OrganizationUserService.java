package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.PaginationHelper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationUserService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final AutoMapper autoMapper;
    private final EmailService emailService;
    private final InvitationHelper invitationHelper;

    public Page<?> findUsersByOrganization(Long id, int page, int size) {
        var organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        List<UserDetailsDto> allUsers = organization.getUserList()
                .stream().map(autoMapper::mapToUserDisplayDTO).toList();
        return PaginationHelper.toPage(page, size, allUsers);
    }

    public OrganizationDTO addUser(Long id, String hash) {
        var organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        if (!organization.getInvitationHash().equals(hash)) throw new WrongInvitationException();

        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(organization.getUserList().stream().anyMatch(requestUser::equals)) throw new AlreadyInOrganizationException();

        UserOrganizationService.addUser(requestUser, organization);
        return autoMapper.mapToOrganizationDto(organizationRepository.save(organization));
    }

    public OrganizationDTO addUserThroughEmail(Long id, String email) {
        var organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        String subject = "Invitation to " + organization.getName();
        String body = invitationHelper.createInvitationLink(organization);
        emailService.sendSimpleMessage(email, subject, body);

        return autoMapper.mapToOrganizationDto(organization);
    }

    public Set<UserDetailsDto> viewAllUsers(Long id) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.userIsInOrganization(organization, requestUser);
        return userRepository.getAllUsersByOrganizationId(id)
                .stream().map(u -> new UserDetailsDto(u.get(0, Long.class),
                                                      u.get(1, String.class),
                                                      u.get(2, String.class),
                                                      u.get(3, String.class),
                                                      u.get(4, LocalDateTime.class)))
                .collect(Collectors.toSet());
    }
}
