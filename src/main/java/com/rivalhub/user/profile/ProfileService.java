package com.rivalhub.user.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.OrganizationMapper;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserProfileHelper userProfileHelper;

    private final MergePatcher<UserDetailsDto> userDetailsDtoMergePatcher;
    private final AutoMapper autoMapper;
    Set<ReservationInProfileDTO> getSharedOrganizationReservations(Long id) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        UserData viewedUser = userRepository.findById(id)
                .orElseThrow(UserAlreadyExistsException::new);

        return userProfileHelper.getReservationsInSharedOrganizations(requestUser, viewedUser);
    }

    Set<EventProfileDTO> getSharedOrganizationEvents(Long id) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        UserData viewedUser = userRepository.findById(id)
                .orElseThrow(UserAlreadyExistsException::new);

        return userProfileHelper.getEventsInSharedOrganizations(requestUser, viewedUser);
    }

    public UserDetailsDto updateProfile(JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        UserDetailsDto userDTO = autoMapper.mapToUserDetails(requestUser);
        UserDetailsDto userDetailsDto = userDetailsDtoMergePatcher.patch(patch, userDTO, UserDetailsDto.class);
        userRepository.save(autoMapper.mapToUserData(userDetailsDto));

        return userDetailsDto;
    }
}
