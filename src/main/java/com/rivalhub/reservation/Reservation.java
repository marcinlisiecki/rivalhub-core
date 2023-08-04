package com.rivalhub.reservation;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonManagedReference
    @JoinTable(name = "user_reservations",
            joinColumns = @JoinColumn(name = "reservation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private UserData userData;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonManagedReference
    @JoinTable(name = "reservations_stations",
            joinColumns = @JoinColumn(name = "reservation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "station_id", referencedColumnName = "id")
    )
    private List<Station> stationList = new ArrayList<>();
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endTime;

    public Reservation(UserData userData, List<Station> stationList, LocalDateTime startTime, LocalDateTime endTime) {
        this.userData = userData;
        this.stationList = stationList;
        this.startTime = startTime;
        this.endTime = endTime;
    }

//    private Event event


}
