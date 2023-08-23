package com.rivalhub.reservation;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.organization.Organization;
import com.rivalhub.common.exception.ReservationIsNotPossible;
import com.rivalhub.station.Station;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
public class ReservationValidator {

    public static boolean checkIfStationsAreInOrganization(List<Station> stationList, Organization organization){
        return stationList.stream()
                .allMatch(organization.getStationList()::contains);
    }

    public static boolean checkIfReservationIsPossible(AddReservationDTO reservationDTO, Organization organization, List<Station> stationList) {
        if (!ReservationValidator.checkIfStationsAreInOrganization(stationList, organization)) return false;
        if (ReservationValidator.checkForTimeCollision(stationList, reservationDTO.getStartTime(), reservationDTO.getEndTime())) return false;

        return true;
    }


    public static void checkIfReservationIsPossible(AddReservationDTO addReservationDTO, List<Station> stations) {
        if (checkForTimeCollision(stations, addReservationDTO.getStartTime(), addReservationDTO.getEndTime())) {
            throw new ReservationIsNotPossible();
        }
    }

    private static boolean checkForTimeCollision(List<Station> stationList, String startTime, String endTime){
        List<List<LocalDateTime>> listsOfStartTime = stationList.stream().map(station
                -> station.getReservationList()
                .stream().map(Reservation::getStartTime)
                .toList()).toList();
        List<List<LocalDateTime>> listsOfEndTime = stationList.stream().map(station
                -> station.getReservationList()
                .stream().map(Reservation::getEndTime)
                .toList()).toList();

        List<LocalDateTime> listOfStartTime =
                listsOfStartTime.stream()
                        .flatMap(List::stream).toList();

        List<LocalDateTime> listOfEndTime =
                listsOfEndTime.stream()
                        .flatMap(List::stream).toList();

        List<Instant> instantsStartTime = listOfStartTime
                .stream().map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant())
                .toList();
        List<Instant> instantsEndTime = listOfEndTime
                .stream().map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant())
                .toList();

        final var startTimeFormatted = LocalDateTime.parse(startTime, FormatterHelper.formatter());
        final var endTimeFormatted = LocalDateTime.parse(endTime, FormatterHelper.formatter());

        long reservationStartTime =  startTimeFormatted.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long reservationEndTime =  endTimeFormatted.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        boolean isCollision;
        for(int i = 0; i < instantsStartTime.size(); i++){
            isCollision = true;
            long timeStart = instantsStartTime.get(i).toEpochMilli();
            long timeEnd = instantsEndTime.get(i).toEpochMilli();

            long timeStartReservationStartControl = timeStart - reservationStartTime;
            long timeStartReservationEndControl = timeStart - reservationEndTime;

            long timeEndReservationStartControl = timeEnd - reservationStartTime;
            long timeEndReservationEndControl = timeEnd - reservationEndTime;


            if (timeStartReservationStartControl > 0 && timeStartReservationEndControl > 0
                    && timeEndReservationStartControl > 0 && timeEndReservationEndControl > 0) isCollision = false;
            if (timeStartReservationStartControl < 0 && timeStartReservationEndControl < 0
                    && timeEndReservationStartControl < 0 && timeEndReservationEndControl < 0) isCollision = false;

            if (!isCollision) continue;
            return true;
        }
        return false;
    }

}
