package com.rivalhub.user.profile;

import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final RepositoryManager repositoryManager;
    private final UserProfileHelper userProfileHelper;

    List<ReservationDTO> getSharedOrganizationReservations(Long id, String loggedUserEmail) {
        UserData loggedUser = repositoryManager.findUserByEmail(loggedUserEmail);
        UserData viewedUser = repositoryManager.findUserById(id);

        return userProfileHelper.getReservationsInSharedOrganizations(loggedUser, viewedUser);
    }
}
