package com.rivalhub.station;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.event.EventType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = ErrorMessages.EVENT_TYPE_IS_REQUIRED)
    @Enumerated(EnumType.STRING)
    private EventType type;

    @NotNull(message = ErrorMessages.NAME_IS_REQUIRED)
    @Size(min = 2, max = 256, message = ErrorMessages.NAME_SIZE)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "stationList")
    @JsonBackReference("reservation-stations")
    private List<Reservation> reservationList = new ArrayList<>();

    private boolean isActive;

    public Station(Long id, EventType type) {
        this.id = id;
        this.type = type;
    }

    public void addReservation(Reservation reservation) {
        this.reservationList.add(reservation);
    }

}
