package com.rivalhub.event.PingPong;

import com.rivalhub.event.EventDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTOMapper;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PingPongEventDtoMapper {
    final OrganizationDTOMapper organizationDTOMapper;
    final UserDtoMapper userDtoMapper;
    PingPongEvent map(EventDto eventDto){
        PingPongEvent event = new PingPongEvent();
        event.setOrganization(organizationDTOMapper.map(eventDto.getOrganization()));
//        event.setEventId(eventDto.getEventId());
//        event.setParticipants(userDtoMapper.map(eventDto.getParticipants()));
//        event.setReservation(eventDto.getReservation());
//        event.setHost(eventDto.getHost());
        event.setEndTime(eventDto.getEndTime());
        event.setEndTime(eventDto.getStartTime());
        return  event;
    }

    EventDto map(PingPongEvent pingPongEvent){
        EventDto eventDto = new EventDto();
//        eventDto.setOrganization(pingPongEvent.getOrganization());
//        eventDto.setEventId(pingPongEvent.getEventId());
//        eventDto.setParticipants(pingPongEvent.getParticipants());
//        eventDto.setReservation(pingPongEvent.getReservation());
//        eventDto.setHost(pingPongEvent.getHost());
        eventDto.setEndTime(pingPongEvent.getEndTime());
        eventDto.setEndTime(pingPongEvent.getStartTime());
        return  eventDto;
    }
}
