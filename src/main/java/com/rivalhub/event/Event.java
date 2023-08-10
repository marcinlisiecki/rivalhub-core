package com.rivalhub.event;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import javassist.expr.NewArray;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@MappedSuperclass
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long eventId;
    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    @JoinColumn(name = "reservation_id")
    Reservation reservation;
    LocalDateTime startTime;
    LocalDateTime endTime;
    @ManyToOne
    UserData host;
    @OneToMany
    List<UserData> participants;
    @ManyToOne
    Organization organization;


}
