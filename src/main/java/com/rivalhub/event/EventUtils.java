package com.rivalhub.event;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.common.exception.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class EventUtils {

    public static AddReservationDTO createAddReservationDTO(EventDto eventDto, Organization organization) {
        return AddReservationDTO.builder()
                .endTime(eventDto.getEndTime())
                .startTime(eventDto.getStartTime())
                .stationsIdList(eventDto.getStationList())
                .organizationId(organization.getId())
                .build();
    }

    public static UserData getHost(Organization organization, Long host) {
        return organization.getUserList()
                .stream().filter(userData -> userData.getId().equals(host))
                .findFirst()
                .orElseThrow(UserAlreadyExistsException::new);
    }

    public static Predicate<UserData> usersExistingInOrganization(EventDto eventDto) {
        return userData -> eventDto.getParticipants().contains(userData.getId());
    }

    public static Predicate<UserData> getUserFromOrganization(List<Long> MatchDTOUsersIdList) {
        return userData -> MatchDTOUsersIdList.contains(userData.getId());
    }

    public static void setBasicInfo(Event event, Organization organization, EventDto eventDto, Reservation reservation) {
        List<UserData> participants =
                organization.getUserList()
                        .stream().filter(EventUtils.usersExistingInOrganization(eventDto))
                        .toList();

        event.getParticipants().addAll(participants);
        event.setHost(EventUtils.getHost(organization, eventDto.getHost()));

        event.setEventPublic(eventDto.isEventPublic());
        event.setStartTime(LocalDateTime.parse(eventDto.getStartTime(), FormatterHelper.formatter()));
        event.setEndTime(LocalDateTime.parse(eventDto.getEndTime(), FormatterHelper.formatter()));
        event.setReservation(reservation);
        event.setName(eventDto.getName());
        event.setDescription(event.getDescription());
    }
}
