package com.rivalhub.common;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.station.StationDTO;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoMapper {
    private final ModelMapper modelMapper;
    public UserData mapToUserData(UserDto userDto){
        return modelMapper.map(userDto, UserData.class);
    }
    public UserDto mapToUserDto(UserData userData){
        return modelMapper.map(userData, UserDto.class);
    }

    public UserData mapToUserData(UserDetails userDetailsDto){
        return modelMapper.map(userDetailsDto, UserData.class);
    }

    public UserDetailsDto mapToUserDetails(UserData userData){
        return modelMapper.map(userData,UserDetailsDto.class);
    }

    public Station mapToStation(StationDTO stationDTO){
        return modelMapper.map(stationDTO,Station.class);
    }

    public StationDTO mapToNewStationDto(Station station){
        return modelMapper.map(station, StationDTO.class);
    }

    public Reservation mapToReservation(ReservationDTO reservationDTO){
        return modelMapper.map(reservationDTO,Reservation.class);
    }

    public Reservation mapToReservation(AddReservationDTO addReservationDTO){
        return modelMapper.map(addReservationDTO,Reservation.class);
    }

    public Organization mapToOrganization(OrganizationDTO organizationDTO){
        return modelMapper.map(organizationDTO,Organization.class);
    }

    public Organization mapToOrganization(OrganizationCreateDTO organizationCreateDTO){
        return modelMapper.map(organizationCreateDTO,Organization.class);
    }


    public OrganizationDTO mapToOrganizationDto(Organization organization) {
        return modelMapper.map(organization,OrganizationDTO.class);
    }


    public ReservationDTO mapToReservationDto(Reservation reservation) {
        return modelMapper.map(reservation,ReservationDTO.class);
    }

    public UserDetailsDto mapToUserDisplayDTO(UserData userData) {
        return modelMapper.map(userData, UserDetailsDto.class);

    }
}
