package com.rivalhub.station;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.event.EventType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @JsonBackReference
    private List<Reservation> reservationList = new ArrayList<>();

    private boolean isActive;

    public Station(Long id, EventType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station station)) return false;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
