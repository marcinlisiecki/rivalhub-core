package com.rivalhub.reservation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {

    @Query(value = "SELECT END_TIME, ID, START_TIME, USER_ID, STATION_ID FROM RESERVATION \n" +
            "JOIN RESERVATIONS_STATIONS ON RESERVATIONS_STATIONS.RESERVATION_ID=RESERVATION.ID \n" +
            "JOIN ORGANIZATION_STATION_LIST ON ORGANIZATION_STATION_LIST.STATION_LIST_ID=STATION_ID \n" +
            "JOIN USER_RESERVATIONS ON ID = USER_RESERVATIONS.RESERVATION_ID \n" +
            "WHERE ORGANIZATION_ID = :id", nativeQuery = true)
    List<Reservation> reservationsByOrganization(@Param("id") Long id);
}
