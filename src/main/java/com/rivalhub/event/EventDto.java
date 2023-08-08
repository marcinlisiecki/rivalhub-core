package com.rivalhub.event;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDto;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
    List<UserDto> participants;
    OrganizationDTO organization;
}
