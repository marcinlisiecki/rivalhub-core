package com.rivalhub.event;

import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.user.UserAlreadyExistsException;
import com.rivalhub.user.UserData;

import java.util.List;
import java.util.function.Predicate;

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
}
