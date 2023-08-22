package com.rivalhub.user.profile;

import com.rivalhub.security.SecurityUtils;
import com.rivalhub.common.exception.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserProfileHelper userProfileHelper;

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
}
