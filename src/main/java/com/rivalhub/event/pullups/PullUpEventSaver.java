package com.rivalhub.event.pullups;

import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventUtils;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PullUpEventSaver {
    private final OrganizationRepository organizationRepository;
    private final OrganizationReservationService reservationService;
    PullUpEvent saveEvent(PullUpEvent pullUpEvent, Organization organization, EventDto eventDto) {
        AddReservationDTO addReservationDTO = EventUtils.createAddReservationDTO(eventDto, organization);
        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);
        EventUtils.setBasicInfo(pullUpEvent,organization,eventDto,reservation);
        addPullUpEventTo(organization, pullUpEvent);
        organizationRepository.save(organization);
        return pullUpEvent;
    }

    private void addPullUpEventTo(Organization organization, PullUpEvent pullUpEvent) {
        organization.getPullUpsEvents().add(pullUpEvent);
    }
}
