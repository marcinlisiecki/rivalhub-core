package com.rivalhub.event.darts;

import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventUtils;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DartEventSaver {
    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;

    DartEvent saveEvent(DartEvent dartEvent, Organization organization, EventDto eventDto) {
        AddReservationDTO addReservationDTO = EventUtils.createAddReservationDTO(eventDto, organization);
        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);
        EventUtils.setBasicInfo(dartEvent,organization,eventDto,reservation);
        addDartEventTo(organization, dartEvent);
        organizationRepository.save(organization);
        return dartEvent;
    }



    private void addDartEventTo(Organization organization, DartEvent dartEvent) {
        organization.getDartEvents().add(dartEvent);
    }


}
