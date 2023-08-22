package com.rivalhub.organization.service;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.PaginationHelper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserNotFoundException;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

    public Page<?> findUsersByOrganization(Long id, int page, int size) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);

        List<UserDetailsDto> allUsers = organization.getUserList()
                .stream().map(autoMapper::mapToUserDisplayDTO)
                .toList();
        return PaginationHelper.toPage(page, size, allUsers);
    }

    public OrganizationDTO addUser(Long id, String hash) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);

        if (!organization.getInvitationHash().equals(hash)) throw new WrongInvitationException();

        var requestUser = SecurityUtils.getUserFromSecurityContext();

        if(organization.getUserList().stream().anyMatch(requestUser::equals)) throw new AlreadyInOrganizationException();

        UserOrganizationService.addUser(requestUser, organization);
        return autoMapper.mapToOrganizationDto(organizationRepository.save(organization));
    }

    public OrganizationDTO addUserThroughEmail(Long id, String email) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        emailService.sendEmailWithInvitationToOrganization(email, organization);

        return autoMapper.mapToOrganizationDto(organization);
    }

    public Set<UserDetailsDto> viewAllUsers(Long id) {
        return userRepository.getAllUsersByOrganizationId(id)
                .stream().map(u -> new UserDetailsDto(u.get(0, Long.class),
                                                      u.get(1, String.class),
                                                      u.get(2, String.class),
                                                      u.get(3, String.class)))
                .collect(Collectors.toSet());
    }

    public void deleteUserFromOrganization(Long organizationId, Long userId) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        var userToDelete = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        UserOrganizationService.deleteUserFrom(organization, userToDelete);

        organizationRepository.save(organization);
    }

    public List<UserDetailsDto> findUsersByNamePhrase(Long id, String namePhrase) {
        return userRepository.findByNamePhraseAndOrganizationId(id, "%" + namePhrase + "%")
                .stream().map(u -> new UserDetailsDto(u.get(0, Long.class),
                        u.get(1, String.class),
                        u.get(2, String.class),
                        u.get(3, String.class)))
                .collect(Collectors.toList());
    }

    public List<UserDetailsDto> findAdminUsersByOrganization(Long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        var adminUsers = organization.getAdminUsers();
        return adminUsers
                .stream().map(u -> new UserDetailsDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getProfilePictureUrl(),
                        u.getActivationTime()
                ))
                .collect(Collectors.toList());
    }
}
