package com.rivalhub.common;

import com.rivalhub.event.EventDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.station.NewStationDto;
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
        UserData userData = modelMapper.map(userDto, UserData.class);
        return userData;
    }
    public UserDto mapToUserDto(UserData userData){
        UserDto userDto = modelMapper.map(userData, UserDto.class);
        return userDto;
    }

    public UserData mapToUserData(UserDetails userDetailsDto){
        UserData userData = modelMapper.map(userDetailsDto, UserData.class);
        return  userData;
    }

    public UserDetailsDto mapToUserDetails(UserData userData){
        UserDetailsDto userDetailsDto = modelMapper.map(userData,UserDetailsDto.class);
        return userDetailsDto;
    }

    public Station mapToStation(NewStationDto newStationDto){
        Station station = modelMapper.map(newStationDto,Station.class);
        return station;
    }

    public NewStationDto mapToNewStationDto(Station station){
        NewStationDto newStationDto = modelMapper.map(station,NewStationDto.class);
        return newStationDto;
    }

    public Reservation mapToReservation(ReservationDTO reservationDTO){
        Reservation reservation = modelMapper.map(reservationDTO,Reservation.class);
        return reservation;
    }

    public Reservation mapToReservation(AddReservationDTO addReservationDTO){
        Reservation reservation = modelMapper.map(addReservationDTO,Reservation.class);
        return reservation;
    }

    public Organization mapToOrganization(OrganizationDTO organizationDTO){
        Organization organization = modelMapper.map(organizationDTO,Organization.class);
        return organization;
    }

    public Organization mapToOrganization(OrganizationCreateDTO organizationCreateDTO){
        Organization organization = modelMapper.map(organizationCreateDTO,Organization.class);
        return organization;
    }


    public OrganizationDTO mapToOrganizationDto(Organization organization) {
        OrganizationDTO organizationDTO = modelMapper.map(organization,OrganizationDTO.class);
        return organizationDTO;
    }


    public ReservationDTO mapToReservationDto(Reservation reservation) {
        ReservationDTO reservationDTO = modelMapper.map(reservation,ReservationDTO.class);
        return reservationDTO;
    }

    public PingPongEvent mapToPingPongEvent(EventDto eventDto){
        PingPongEvent pingPongEvent = modelMapper.map(eventDto,PingPongEvent.class);
        return pingPongEvent;
    }

    public EventDto mapToEventDto(PingPongEvent pingPongEvent){
        EventDto eventDto = modelMapper.map(pingPongEvent,EventDto.class);
        return eventDto;
    }
}
