package com.rivalhub.event.PingPong;

import com.rivalhub.event.EventDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PingPongEventDtoMapper {
    PingPongEvent map(EventDto eventDto){
        PingPongEvent event = new PingPongEvent();
        event.setOrganization(eventDto.getOrganization());
        event.setEventId(eventDto.getEventId());
        event.setParticipants(eventDto.getParticipants());
        event.setReservation(eventDto.getReservation());
        event.setHost(eventDto.getHost());
        event.setEndTime(eventDto.getEndTime());
        event.setEndTime(eventDto.getStartTime());
        return  event;
    }

    EventDto map(PingPongEvent pingPongEvent){
        EventDto eventDto = new EventDto();
        eventDto.setOrganization(pingPongEvent.getOrganization());
        eventDto.setEventId(pingPongEvent.getEventId());
        eventDto.setParticipants(pingPongEvent.getParticipants());
        eventDto.setReservation(pingPongEvent.getReservation());
        eventDto.setHost(pingPongEvent.getHost());
        eventDto.setEndTime(pingPongEvent.getEndTime());
        eventDto.setEndTime(pingPongEvent.getStartTime());
        return  eventDto;
    }
}
