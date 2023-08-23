package com.rivalhub.user.profile;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UserProfileHelper {
    private final OrganizationRepoManager organizationRepoManager;
    private final AutoMapper autoMapper;
    Set<ReservationInProfileDTO> getReservationsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Long> sharedOrganizationIds = getSharedOrganizationList(loggedUser, viewedUser);

        List<Organization> sharedOrganizations = organizationRepoManager.findAllOrganizationsByIds(sharedOrganizationIds);
        Set<ReservationInProfileDTO> reservationDTOs = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            List<ReservationInProfileDTO> reservations = organizationRepoManager
                    .reservationsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                    .stream().map(setReservationInProfileDTO(sharedOrganization))
                    .toList();

            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }

    Set<EventProfileDTO> getEventsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Long> sharedOrganizationIds = getSharedOrganizationList(loggedUser, viewedUser);
        List<Organization> sharedOrganizations = organizationRepoManager.findAllOrganizationsByIds(sharedOrganizationIds);

        Set<EventProfileDTO> eventList = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            Set<PingPongEvent> events = organizationRepoManager.
                    eventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

            List<EventProfileDTO> eventProfileDTOStream = events.stream().map(setEventProfileDTO()).toList();
            eventList.addAll(eventProfileDTOStream);
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

    private Function<Reservation, ReservationInProfileDTO> setReservationInProfileDTO(Organization sharedOrganization) {
        return reservation -> {
            ReservationInProfileDTO reservationDTO = autoMapper.mapToShowReservationInProfileDTO(reservation);
            reservationDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            return reservationDTO;
        };
    }

    private List<Long> getSharedOrganizationList(UserData loggedUser, UserData viewedUser) {
        return organizationRepoManager.getSharedOrganizationIds(loggedUser.getId(), viewedUser.getId());
    }
}
