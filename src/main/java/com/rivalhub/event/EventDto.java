package com.rivalhub.event;

import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventDto {
    Long eventId;
    Reservation reservation;
    LocalDateTime startTime;
    LocalDateTime endTime;
    UserData host;
    List<UserData> participants;
    Organization organization;
}
