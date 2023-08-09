package com.rivalhub.reservation;

import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.NewStationDtoMapper;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserDtoDetailsMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationMapper {

    private final UserDtoDetailsMapper userDtoDetailsMapper = new UserDtoDetailsMapper();

    public ReservationDTO map(Reservation reservation){
        ReservationDTO reservationDTO = new ReservationDTO();
        UserDetailsDto userDetailsDto = userDtoDetailsMapper.mapUserToReservationDTO(reservation.getUserData());

        List<NewStationDto> stationDtoList = reservation.getStationList()
                .stream().map(NewStationDtoMapper::map).toList();

        reservationDTO.setEndTime(reservation.getEndTime().toString());
        reservationDTO.setStartTime(reservation.getStartTime().toString());
        reservationDTO.setUser(userDetailsDto);
        reservationDTO.setId(reservation.getId());
        reservationDTO.setStationList(stationDtoList);

        return reservationDTO;
    }
}
