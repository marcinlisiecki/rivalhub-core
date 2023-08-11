package com.rivalhub.organization;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.PaginationHelper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.user.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserRepository;
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
    private final OrganizationRepository organizationRepository;
    private final AutoMapper autoMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InvitationHelper invitationHelper;

    Page<?> findUsersByOrganization(Long id, int page, int size) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        List<UserDetailsDto> allUsers = organization.getUserList()
                .stream().map(autoMapper::mapToUserDisplayDTO).toList();
        return PaginationHelper.toPage(page, size, allUsers);
    }

    OrganizationDTO addUser(Long id, String hash, String email) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        if (!organization.getInvitationHash().equals(hash)) throw new WrongInvitationException();

        UserData user = userRepository.findByEmail(email)
                .map(userData -> checkIfUserIsInOrganization(userData, organization))
                .orElseThrow(AlreadyInOrganizationException::new);

        UserOrganizationService.addUser(user, organization);
        Organization save = organizationRepository.save(organization);

        return autoMapper.mapToOrganizationDto(save);
    }

    private UserData checkIfUserIsInOrganization(UserData user, Organization organization) {
        if (user.getOrganizationList().contains(organization)) throw new AlreadyInOrganizationException();
        return user;
    }

    OrganizationDTO addUserThroughEmail(Long id, String email) {
        OrganizationDTO organizationDTO = organizationRepository
                .findById(id)
                .map(autoMapper::mapToOrganizationDto)
                .orElseThrow(OrganizationNotFoundException::new);
        String subject = "Invitation to " + organizationDTO.getName();

        String body = invitationHelper.createInvitationLink(organizationDTO);
        emailService.sendSimpleMessage(email, subject, body);

        return organizationDTO;
    }

    Set<UserDetailsDto> viewAllUsers(Long id, String email) {
        UserData user = userRepository.findByEmail(email).orElseThrow(UserAlreadyExistsException::new);
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.userIsInOrganization(organization, user);

        return userRepository.getAllUsersByOrganizationId(id)
                .stream().map(u -> new UserDetailsDto(u.get(0, Long.class),
                                                      u.get(1, String.class),
                                                      u.get(2, String.class),
                                                      u.get(3, String.class),
                                                      u.get(4, LocalDateTime.class)
                        )).collect(Collectors.toSet());
    }
}
