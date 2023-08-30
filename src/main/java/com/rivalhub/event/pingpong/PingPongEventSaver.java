package com.rivalhub.event.pingpong;

import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventUtils;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PingPongEventSaver {
    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;

    PingPongEvent saveEvent(PingPongEvent pingPongEvent, Organization organization, EventDto eventDto) {
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
