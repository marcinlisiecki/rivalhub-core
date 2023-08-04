package com.rivalhub.reservation;

import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReservationSaver {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    public ReservationDTO saveReservation(UserData user, List<Station> stationList, AddReservationDTO reservationDTO){
        Reservation reservation = new Reservation(user, stationList,
                LocalDateTime.parse(reservationDTO.getStartTime(), formatter),
                LocalDateTime.parse(reservationDTO.getEndTime(), formatter));

        stationList.forEach(station -> station.addReservation(reservation));
        reservationRepository.save(reservation);

        return reservationMapper.map(reservation);
    }
}
