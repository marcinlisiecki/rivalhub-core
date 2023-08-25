package com.rivalhub.reservation;

import com.rivalhub.event.EventDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {
    @Query(value = """
            SELECT DISTINCT ID, USER_ID, STATION_ID, START_TIME, END_TIME FROM RESERVATION
            JOIN RESERVATIONS_STATIONS ON RESERVATIONS_STATIONS.RESERVATION_ID=RESERVATION.ID
            JOIN ORGANIZATION_STATION_LIST ON ORGANIZATION_STATION_LIST.STATION_LIST_ID=STATION_ID
            JOIN USER_RESERVATIONS ON ID = USER_RESERVATIONS.RESERVATION_ID
            WHERE ORGANIZATION_ID = :organizationId AND USER_ID= :userId
            """, nativeQuery = true)
    Set<Reservation> reservationsByOrganizationIdAndUserId(@Param("organizationId") Long organizationId, @Param("userId") Long userId);


    @Query(value = """
            SELECT DISTINCT ID, USER_ID, STATION_ID, START_TIME, END_TIME FROM RESERVATION\s
            JOIN RESERVATIONS_STATIONS ON RESERVATIONS_STATIONS.RESERVATION_ID = RESERVATION.ID\s
            JOIN ORGANIZATION_STATION_LIST ON ORGANIZATION_STATION_LIST.STATION_LIST_ID = STATION_ID\s
            JOIN USER_RESERVATIONS ON ID = USER_RESERVATIONS.RESERVATION_ID\s
            WHERE ORGANIZATION_ID = :organizationId AND USER_ID = :userId
            """, nativeQuery = true)
    Set<Reservation> reservationsWithParticipantsByOrganizationIdAndUserIdWithFilterByDate(@Param("organizationId") Long organizationId, @Param("userId") Long userId);
}
