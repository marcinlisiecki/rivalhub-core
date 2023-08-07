package com.rivalhub.reservation;

import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserDtoDetailsMapper;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private UserDtoDetailsMapper userDtoDetailsMapper = new UserDtoDetailsMapper();

    public ReservationDTO map(Reservation reservation){
        ReservationDTO reservationDTO = new ReservationDTO();
        UserDetailsDto userDetailsDto = userDtoDetailsMapper.mapUserToReservationDTO(reservation.getUserData());

        reservationDTO.setEndTime(reservation.getEndTime().toString());
        reservationDTO.setStartTime(reservation.getStartTime().toString());
        reservationDTO.setStationList(reservation.getStationList());
        reservationDTO.setUser(userDetailsDto);
        reservationDTO.setId(reservation.getId());

        return reservationDTO;
    }

}
