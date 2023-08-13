package com.rivalhub.user.profile;

import com.rivalhub.event.EventDto;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final RepositoryManager repositoryManager;
    private final UserProfileHelper userProfileHelper;

    Set<ReservationDTO> getSharedOrganizationReservations(Long id, String loggedUserEmail) {
        UserData loggedUser = repositoryManager.findUserByEmail(loggedUserEmail);
        UserData viewedUser = repositoryManager.findUserById(id);

        return userProfileHelper.getReservationsInSharedOrganizations(loggedUser, viewedUser);
    }

    Set<EventDto> getSharedOrganizationEvents(Long id, String loggedUserEmail) {
        UserData loggedUser = repositoryManager.findUserByEmail(loggedUserEmail);
        UserData viewedUser = repositoryManager.findUserById(id);

        return userProfileHelper.getEventsInSharedOrganizations(loggedUser, viewedUser);
    }
}
