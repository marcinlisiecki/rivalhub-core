package com.rivalhub.event;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@MappedSuperclass
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String name;
    private String description;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JsonManagedReference
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endTime;

    @ManyToOne
    private UserData host;

    @ManyToMany
    private List<UserData> participants = new ArrayList<>();

    @ManyToOne
    private Organization organization;
}
