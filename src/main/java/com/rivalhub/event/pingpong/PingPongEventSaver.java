package com.rivalhub.event.pingpong;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventUtils;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserAlreadyExistsException;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class PingPongEventSaver {
    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;

    PingPongEvent saveEvent(PingPongEvent pingPongEvent, Organization organization, EventDto eventDto) {
        //TODO Narazie można dodać tylko użytkowników z danej organizacji!
        AddReservationDTO addReservationDTO = EventUtils.createAddReservationDTO(eventDto, organization);
        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);
        EventUtils.setBasicInfo(pingPongEvent,organization,eventDto,reservation);
        addPingPongEventTo(organization, pingPongEvent);
        organizationRepository.save(organization);
        return pingPongEvent;
    }



    private void addPingPongEventTo(Organization organization, PingPongEvent pingPongEvent) {
        organization.getPingPongEvents().add(pingPongEvent);
    }

}
