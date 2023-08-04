package com.rivalhub.station;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
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

    public Station(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public void addReservation(Reservation reservation) {
        this.reservationList.add(reservation);
    }

    @ManyToOne
    @NotNull(message = ErrorMessages.ORGANIZATION_ID_IS_REQUIRED)
    private Organization organization;
}
