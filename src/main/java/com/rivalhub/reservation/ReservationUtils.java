package com.rivalhub.reservation;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

public class ReservationUtils {

    public static List<Reservation> getSortedReservations(List<Reservation> reservations) {
        return reservations.stream()
                .sorted(Comparator.comparing(Reservation::getStartTime))
                .toList();
    }
}
