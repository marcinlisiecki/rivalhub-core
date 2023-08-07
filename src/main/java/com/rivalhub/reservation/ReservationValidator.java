package com.rivalhub.reservation;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationValidator {
    public static boolean checkForTimeCollision(List<Station> stationList, Reservation reservation){
        List<List<LocalDateTime>> listsOfStartTime = stationList.stream().map(station
                -> station.getReservationList().stream().map(Reservation::getStartTime).toList()).toList();
        List<List<LocalDateTime>> listsOfEndTime = stationList.stream().map(station
                -> station.getReservationList().stream().map(Reservation::getEndTime).toList()).toList();

        List<LocalDateTime> listOfStartTime =
                listsOfStartTime.stream()
                        .flatMap(List::stream).toList();

        List<LocalDateTime> listOfEndTime =
                listsOfEndTime.stream()
                        .flatMap(List::stream).toList();

        List<Instant> instantsStartTime = listOfStartTime.stream().map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant()).toList();
        List<Instant> instantsEndTime = listOfEndTime.stream().map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant()).toList();


        long reservationStartTime =  reservation.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long reservationEndTime =  reservation.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        boolean tmp;
        for(int i = 0; i < instantsStartTime.size(); i++){
            tmp = false;
            long timeStart = instantsStartTime.get(i).toEpochMilli();
            long timeEnd = instantsEndTime.get(i).toEpochMilli();

            long timeStartReservationStartControl = timeStart - reservationStartTime;
            long timeStartReservationEndControl = timeStart - reservationEndTime;

            long timeEndReservationStartControl = timeEnd - reservationStartTime;
            long timeEndReservationEndControl = timeEnd - reservationEndTime;


            if (timeStartReservationStartControl > 0 && timeStartReservationEndControl > 0
                    && timeEndReservationStartControl > 0 && timeEndReservationEndControl > 0) tmp = true;
            if (timeStartReservationStartControl < 0 && timeStartReservationEndControl < 0
                    && timeEndReservationStartControl < 0 && timeEndReservationEndControl < 0) tmp = true;

            if (tmp) continue;
            return true;
        }
        return false;
    }

    public static boolean checkIfStationsAreInOrganization(List<Station> stationList, Organization organization){
        return stationList.stream().allMatch(organization.getStationList()::contains);
    }

    public static boolean checkIfReservationIsPossible(AddReservationDTO reservationDTO, Organization organization,
                                                UserData user, Long id, List<Station> stationList) {

        user.getOrganizationList().stream().filter(org -> org.getId().equals(id))
                .findFirst().orElseThrow(OrganizationNotFoundException::new);

        if (!ReservationValidator.checkIfStationsAreInOrganization(stationList, organization)) return false;

        Reservation reservation = new Reservation(user, stationList,
                LocalDateTime.parse(reservationDTO.getStartTime(), FormatterHelper.formatter()),
                LocalDateTime.parse(reservationDTO.getEndTime(), FormatterHelper.formatter()));
        if (ReservationValidator.checkForTimeCollision(stationList, reservation)) return false;

        return true;
    }

}
