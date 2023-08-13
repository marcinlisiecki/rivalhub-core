package com.rivalhub.user.profile;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProfileHelper {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    public List<ReservationDTO> getReservationsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Organization> sharedOrganizations = loggedUser.getOrganizationList()
                .stream().filter(viewedUser.getOrganizationList()::contains).toList();

        List<ReservationDTO> reservationDTOs = new ArrayList<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            List<ReservationDTO> reservations = repositoryManager
                    .reservationsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                    .stream().map(autoMapper::mapToReservationDto).toList();

            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }
}
