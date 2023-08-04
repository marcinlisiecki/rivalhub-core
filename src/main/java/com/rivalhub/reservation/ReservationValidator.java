package com.rivalhub.reservation;

import com.rivalhub.organization.Organization;
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
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
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
        for(int i = 0; i < instantsStartTime.size(); i++){
            long timeStart = instantsStartTime.get(i).toEpochMilli();
            long timeEnd = instantsEndTime.get(i).toEpochMilli();

            long timeStartReservationStartControl = timeStart - reservationStartTime;
            long timeStartReservationEndControl = timeStart - reservationEndTime;

            long timeEndReservationStartControl = timeEnd - reservationStartTime;
            long timeEndReservationEndControl = timeEnd - reservationEndTime;

            if(!(timeStartReservationStartControl > 0 && timeStartReservationEndControl > 0 &&
                    timeEndReservationStartControl > 0 && timeEndReservationEndControl > 0)
            || !(timeStartReservationStartControl < 0 && timeStartReservationEndControl < 0 &&
                    timeEndReservationStartControl < 0 && timeEndReservationEndControl < 0))  return true;

        }
        return false;
    }

    public static boolean checkIfStationsAreInOrganization(List<Station> stationList, Organization organization){
        return stationList.stream().allMatch(organization.getStationList()::contains);
    }

    public static boolean checkIfReservationIsPossible(AddReservationDTO reservationDTO, Optional<Organization> organization,
                                                UserData user, Long id, List<Station> stationList) {
        if (organization.isEmpty()) return false;

        Organization userOrganization = user.getOrganizationList().stream().filter(org -> org.getId().equals(id)).findFirst().orElse(null);
        if (userOrganization == null) return false;

        if (!ReservationValidator.checkIfStationsAreInOrganization(stationList, organization.get())) return false;

        Reservation reservation = new Reservation(user, stationList,
                LocalDateTime.parse(reservationDTO.getStartTime(), formatter),
                LocalDateTime.parse(reservationDTO.getEndTime(), formatter));
        if (ReservationValidator.checkForTimeCollision(stationList, reservation)) return false;

        return true;
    }

}
