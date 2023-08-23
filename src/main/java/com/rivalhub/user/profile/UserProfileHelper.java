package com.rivalhub.user.profile;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

            List<EventProfileDTO> eventProfileDTOStream = events.stream().map(setEventProfileDTO(sharedOrganization)).toList();
            eventList.addAll(eventProfileDTOStream);
        }

        return eventList;
    }

    private Function<PingPongEvent, EventProfileDTO> setEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
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

    public Set<EventProfileDTO> getEventsByOrganizationsAndDateForRequestUser(UserData requestUser, String date) {
        List<Long> organizationsIdsByUser = organizationRepoManager.getOrganizationsIdsByUser(requestUser.getId());
        List<Organization> userOrganizations = organizationRepoManager.findAllOrganizationsByIds(organizationsIdsByUser);

        LocalDateTime datePattern = LocalDateTime.parse(date, FormatterHelper.formatter());

        Set<EventProfileDTO> eventList = new HashSet<>();

        //TODO DODAĆ RESZTĘ EVENTÓW JAK BĘDĄ JUŻ DZIAŁAĆ
        for (Organization sharedOrganization : userOrganizations) {
            Set<PingPongEvent> events = organizationRepoManager.
                    eventsWithParticipantsByOrganizationIdAndUserIdWithPaginationByDate(sharedOrganization, requestUser.getId(), datePattern);

            List<EventProfileDTO> eventProfileDTOStream = events.stream().map(setEventProfileDTO(sharedOrganization)).toList();
            eventList.addAll(eventProfileDTOStream);
        }
        return eventList;
    }

    public Set<ReservationInProfileDTO> getAllReservationsByRequestUserAndMonth(UserData requestUser, String date) {
        List<Long> organizationsIdsByUser = organizationRepoManager.getOrganizationsIdsByUser(requestUser.getId());
        List<Organization> userOrganizations = organizationRepoManager.findAllOrganizationsByIds(organizationsIdsByUser);

        LocalDateTime datePattern = LocalDateTime.parse(date, FormatterHelper.formatter());

        Set<ReservationInProfileDTO> reservationDTOs = new HashSet<>();

        for (Organization sharedOrganization : userOrganizations) {
            Set<ReservationInProfileDTO> reservations = organizationRepoManager
                    .reservationsByOrganizationIdAndUserIdFilterByDate(sharedOrganization.getId(), requestUser.getId(), datePattern)
                    .stream().map(setReservationInProfileDTO(sharedOrganization))
                    .collect(Collectors.toSet());

            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }
}
