package com.rivalhub.event;

import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.catalina.User;

import java.time.LocalDateTime;
import java.util.List;


@Data
@MappedSuperclass
public class Event {
    @Id
    Long eventId;
    @OneToOne
    Reservation reservation;
    LocalDateTime startTime;
    LocalDateTime endTime;

//    UserData host;
    @OneToMany
    List<UserData> participants;
    @ManyToOne
    Organization organization;
}
