package com.rivalhub.station;

import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationUtils;
import com.rivalhub.reservation.ReservationValidator;
import com.rivalhub.user.UserData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StationAvailabilityFinder {

    public static LocalDateTime getFirstDateAvailableForDuration(List<Station> stations, Duration timeWindow, EventType type) {
        LocalDateTime firstAvailable = null;

        for (Station station : stations) {
            if (station.getType() != type) {
                continue;
            }

            LocalDateTime currentStationFirstAvailable = LocalDateTime.now();
            List<Reservation> reservations = ReservationUtils.getSortedReservations(station.getReservationList());

            if (!reservations.isEmpty()) {
                if (ChronoUnit.SECONDS.between(currentStationFirstAvailable,
                        reservations.get(0).getStartTime().plusMinutes(1)) >= timeWindow.getSeconds()) {

                    if (firstAvailable == null || currentStationFirstAvailable.isBefore(firstAvailable)) {
                        firstAvailable = currentStationFirstAvailable;
                    }

                    break;
                }
            } else {
                firstAvailable = currentStationFirstAvailable;
                break;
            }

            for (int i = 0; i < reservations.size(); i++) {
                Reservation currentReservation = reservations.get(i);

                if (i + 1 >= reservations.size()) {
                    currentStationFirstAvailable = currentReservation.getEndTime().plusMinutes(1);
                    break;
                }

                Reservation nextReservation = reservations.get(i + 1);

                if (ChronoUnit.SECONDS.between(
                        currentReservation.getEndTime().plusMinutes(1),
                        nextReservation.getStartTime().minusMinutes(1)) >= timeWindow.getSeconds()) {

                    currentStationFirstAvailable = currentReservation.getEndTime().plusMinutes(1);
                    break;
                }
            }

            if (firstAvailable == null || currentStationFirstAvailable.isBefore(firstAvailable)) {
                firstAvailable = currentStationFirstAvailable;
            }
        }

        return firstAvailable;
    }


    public static List<Station> getAvailableStations(Organization organization, String startTime,
                                                     String endTime, EventType type) {
        List<Station> availableStations = new ArrayList<>();
        List<Station> stationList = filterForActiveStationsAndTypeIn(organization, type);

        stationList.forEach(station -> {
                    AddReservationDTO reservationDTO = AddReservationDTO.builder()
                            .startTime(startTime)
                            .endTime(endTime)
                            .stationsIdList(List.of(station.getId()))
                            .build();

            if (type != null && !station.getType().equals(type)) {
                return;
            }

            if (ReservationValidator.checkIfReservationIsPossible(
                    reservationDTO,
                    organization,
                    List.of(station))) {

                availableStations.add(station);
            }
        });

        return availableStations;
    }
    private static List<Station> filterForActiveStations(List<Station> stationList) {
        return stationList
                .stream().filter(Station::isActive)
                .toList();
    }

    public static List<Station> filterForTypeIn(List<Station> stationList, EventType type){
        if (type == null) return stationList;
        return stationList
                .stream().filter(station -> station.getType().equals(type))
                .toList();
    }

    public static List<Station> filterForActiveStationsAndTypeIn(Organization organization, EventType type) {
        List<Station> stationList = filterForActiveStations(organization.getStationList());
        return filterForTypeIn(stationList, type);
    }

}
