package com.rivalhub.station;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rivalhub.reservation.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "stationList")
    @JsonBackReference
    private List<Reservation> reservationList = new ArrayList<>();

    public Station(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public void addReservation(Reservation reservation) {
        this.reservationList.add(reservation);
    }
}
