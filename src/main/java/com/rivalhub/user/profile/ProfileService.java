package com.rivalhub.user.profile;

import com.rivalhub.user.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
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

    Set<ReservationInProfileDTO> getSharedOrganizationReservations(Long id) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserData viewedUser = userRepository.findById(id).orElseThrow(UserAlreadyExistsException::new);

        return userProfileHelper.getReservationsInSharedOrganizations(requestUser, viewedUser);
    }

    Set<EventProfileDTO> getSharedOrganizationEvents(Long id) {
        var requestUser = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserData viewedUser = userRepository.findById(id).orElseThrow(UserAlreadyExistsException::new);

        return userProfileHelper.getEventsInSharedOrganizations(requestUser, viewedUser);
    }
}
