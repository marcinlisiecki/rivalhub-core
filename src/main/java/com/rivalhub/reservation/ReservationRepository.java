package com.rivalhub.reservation;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {

    @Query(value = "SELECT DISTINCT ID, USER_ID, STATION_ID, START_TIME, END_TIME FROM RESERVATION \n" +
            "JOIN RESERVATIONS_STATIONS ON RESERVATIONS_STATIONS.RESERVATION_ID=RESERVATION.ID \n" +
            "JOIN ORGANIZATION_STATION_LIST ON ORGANIZATION_STATION_LIST.STATION_LIST_ID=STATION_ID \n" +
            "JOIN USER_RESERVATIONS ON ID = USER_RESERVATIONS.RESERVATION_ID \n" +
            "WHERE ORGANIZATION_ID = :id", nativeQuery = true)
    Set<Reservation> reservationsByOrganization(@Param("id") Long id);
}