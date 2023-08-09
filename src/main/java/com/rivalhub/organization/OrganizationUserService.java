package com.rivalhub.organization;

import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.PaginationHelper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.exception.AlreadyInOrganizationException;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.exception.WrongInvitationException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserDtoMapper;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationUserService {
    private final OrganizationRepository organizationRepository;
    private final UserDtoMapper userDtoMapper;
    private final UserRepository userRepository;
    private final OrganizationDTOMapper organizationDTOMapper;
    private final EmailService emailService;

    Page<?> findUsersByOrganization(Long id, int page, int size) {
        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);

        List<UserDetailsDto> allUsers = organization.getUserList()
                .stream().map(userDtoMapper::mapToUserDisplayDTO).toList();
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

        return organizationDTOMapper.map(save);
    }

    private UserData checkIfUserIsInOrganization(UserData user, Organization organization) {
        if (user.getOrganizationList().contains(organization)) throw new AlreadyInOrganizationException();
        return user;
    }

    OrganizationDTO addUserThroughEmail(Long id, String email) {
        OrganizationDTO organizationDTO = organizationRepository
                .findById(id)
                .map(organizationDTOMapper::map)
                .orElseThrow(OrganizationNotFoundException::new);
        String subject = "Invitation to " + organizationDTO.getName();

        String body = InvitationHelper.createInvitationLink(organizationDTO);
        emailService.sendSimpleMessage(email, subject, body);

        return organizationDTO;
    }


}