package com.rivalhub.common;

import com.rivalhub.auth.LoginRequestDto;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.user.profile.EventProfileDTO;
import com.rivalhub.user.profile.ReservationInProfileDTO;
import com.rivalhub.station.StationDTO;
import com.rivalhub.station.Station;
import com.rivalhub.user.RegisterRequestDto;
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

    public LoginRequestDto mapToLoginRequest(RegisterRequestDto registerRequestDto) {
        return modelMapper.map(registerRequestDto, LoginRequestDto.class);
    }

    public UserData mapToUserData(RegisterRequestDto registerRequestDto) {
        return modelMapper.map(registerRequestDto, UserData.class);
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


    public OrganizationDTO mapToOrganizationDto(Organization organization) {
        return modelMapper.map(organization,OrganizationDTO.class);
    }


    public ReservationDTO mapToReservationDto(Reservation reservation) {
        return modelMapper.map(reservation,ReservationDTO.class);
    }
    public ReservationInProfileDTO mapToShowReservationInProfileDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationInProfileDTO.class);
    }

    public UserDetailsDto mapToUserDisplayDTO(UserData userData) {
        return modelMapper.map(userData, UserDetailsDto.class);

    }
    public PingPongEvent mapToPingPongEvent(EventDto eventDto){
        return modelMapper.map(eventDto,PingPongEvent.class);
    }

    public EventDto mapToEventDto(PingPongEvent pingPongEvent){
        return modelMapper.map(pingPongEvent,EventDto.class);
    }

    public EventDto mapToEventDto(TableFootballEvent tableFootballEvent){
        return modelMapper.map(tableFootballEvent,EventDto.class);
    }

    public EventDto mapToEventDto(PullUpEvent pullUpEvent){
        return modelMapper.map(pullUpEvent,EventDto.class);
    }

    public EventDto mapToEventDto(DartEvent dartEvent){
        return modelMapper.map(dartEvent,EventDto.class);
    }


    public BilliardsEvent mapToBilliardsEvent(EventDto eventDto){
        return modelMapper.map(eventDto,BilliardsEvent.class);
    }

    public EventDto mapToEventDto(BilliardsEvent billiardsEvent){
        return modelMapper.map(billiardsEvent,EventDto.class);
    }

    public UserData mapToUserData(UserDetailsDto userDetailsDto) {
        return modelMapper.map(userDetailsDto, UserData.class);
    }

    public EventProfileDTO mapToEventProfileDTO(PingPongEvent pingPongEvent) {
        return modelMapper.map(pingPongEvent, EventProfileDTO.class);
    }
}
