package com.rivalhub.user.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FileUploadUtil;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.common.exception.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.*;
import com.rivalhub.user.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserProfileHelper userProfileHelper;
    private final MergePatcher<UserDetailsDto> userDetailsDtoMergePatcher;
    private final AutoMapper autoMapper;
    private final FileUploadUtil fileUploadUtil;

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

    public Set<EventProfileDTO> getAllEventsByRequestUserAndMonth(String date) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        return userProfileHelper.getEventsByOrganizationsAndDateForRequestUser(requestUser, date);
    }

    public Set<ReservationInProfileDTO> getAllReservationsByRequestUserAndMonth(String date) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        return userProfileHelper.getAllReservationsByRequestUserAndMonth(requestUser, date);
    }

    public UserDetailsDto updateProfile(JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        UserDetailsDto userDetailsToPatch = autoMapper.mapToUserDetails(requestUser);
        UserDetailsDto userDetailsDto = userDetailsDtoMergePatcher.patch(patch, userDetailsToPatch, UserDetailsDto.class);

        userRepository.save(UserMapper.mapUserDetailsDtoToUserData(userDetailsDto, requestUser));
        return userDetailsDto;
    }

    public void deleteProfile() {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        userRepository.deleteById(requestUser.getId());
    }

    public UserDetailsDto updateImage(MultipartFile multipartFile, boolean keepAvatar) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        fileUploadUtil.updateUserImage(requestUser, multipartFile, keepAvatar);
        userRepository.save(requestUser);

        return UserMapper.map(requestUser);
    }

    public List<Notification> getNotifications() {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var userWithNotifications = userRepository.findUserWithNotifications(requestUser.getId());

        return userWithNotifications.getNotifications()
                .stream().filter(
                        notification -> notification.getStatus()
                                .equals(Notification.Status.NOT_CONFIRMED))
                .toList();

    }
}
