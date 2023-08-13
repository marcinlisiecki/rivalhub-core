package com.rivalhub.user.profile;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.EventDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserProfileHelper {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    Set<ReservationDTO> getReservationsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Organization> sharedOrganizations = getSharedOrganizationList(loggedUser, viewedUser);

        Set<ReservationDTO> reservationDTOs = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            List<ReservationDTO> reservations = repositoryManager
                    .reservationsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                    .stream().map(reservation -> {
                        ReservationDTO reservationDTO = autoMapper.mapToReservationDto(reservation);
                        reservationDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
                        return reservationDTO;
                    })
                    .toList();


            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }

    Set<EventDto> getEventsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Organization> sharedOrganizations = getSharedOrganizationList(loggedUser, viewedUser);

        Set<EventDto> eventList = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            Set<EventDto> events = repositoryManager
                    .eventsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                    .stream().map(autoMapper::mapToEventDto).collect(Collectors.toSet());

            eventList.addAll(events);
        }

        return eventList;
    }

    private static List<Organization> getSharedOrganizationList(UserData loggedUser, UserData viewedUser) {
        return loggedUser.getOrganizationList()
                .stream().filter(viewedUser.getOrganizationList()::contains)
                .toList();
    }
}
