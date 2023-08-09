package com.rivalhub.reservation;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReservationSaver {
    private final ReservationRepository reservationRepository;
    private final AutoMapper autoMapper;


    public ReservationDTO saveReservation(UserData user, List<Station> stationList, AddReservationDTO reservationDTO){
        Reservation reservation = new Reservation(user, stationList,
                LocalDateTime.parse(reservationDTO.getStartTime(), FormatterHelper.formatter()),
                LocalDateTime.parse(reservationDTO.getEndTime(), FormatterHelper.formatter()));

        stationList.forEach(station -> station.addReservation(reservation));
        reservationRepository.save(reservation);

        return autoMapper.mapToReservationDto(reservation);
    }
}
