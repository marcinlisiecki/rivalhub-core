package com.rivalhub.event;


import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventDto {
    Long eventId;
    ReservationDTO reservation;
    LocalDateTime startTime;
    LocalDateTime endTime;
    UserDto host;
    List<Long> participants;
    OrganizationDTO organization;
}
