package com.rivalhub.user.profile;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserProfileHelper {
    private final RepositoryManager repositoryManager;
    private final AutoMapper autoMapper;
    Set<ReservationInProfileDTO> getReservationsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Organization> sharedOrganizations = getSharedOrganizationList(loggedUser, viewedUser);

        Set<ReservationInProfileDTO> reservationDTOs = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            List<ReservationInProfileDTO> reservations = repositoryManager
                    .reservationsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                    .stream().map(setReservationInProfileDTO(sharedOrganization))
                    .toList();

            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }

    private Function<Reservation, ReservationInProfileDTO> setReservationInProfileDTO(Organization sharedOrganization) {
        return reservation -> {
            ReservationInProfileDTO reservationDTO = autoMapper.mapToShowReservationInProfileDTO(reservation);
            reservationDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            return reservationDTO;
        };
    }

    Set<EventProfileDTO> getEventsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Organization> sharedOrganizations = getSharedOrganizationList(loggedUser, viewedUser);

        Set<EventProfileDTO> eventList = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            Set<EventProfileDTO> events =
                    repositoryManager.eventsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                            .stream().map(setEventProfileDTO())
                            .collect(Collectors.toSet());

            eventList.addAll(events);
        }

        return eventList;
    }

    private Function<PingPongEvent, EventProfileDTO> setEventProfileDTO() {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }

    private static List<Organization> getSharedOrganizationList(UserData loggedUser, UserData viewedUser) {
        return loggedUser.getOrganizationList()
                .stream().filter(viewedUser.getOrganizationList()::contains)
                .toList();
    }
}
