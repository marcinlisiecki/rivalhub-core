package com.rivalhub.event.running;

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
public class RunningEventSaver {
    private final OrganizationRepository organizationRepository;
    private final OrganizationReservationService reservationService;
    RunningEvent saveEvent(RunningEvent runningEvent, Organization organization, EventDto eventDto) {
        AddReservationDTO addReservationDTO = EventUtils.createAddReservationDTO(eventDto, organization);
        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);
        EventUtils.setBasicInfo(runningEvent,organization,eventDto,reservation);
        addRunningEventTo(organization, runningEvent);
        organizationRepository.save(organization);
        return runningEvent;
    }

    private void addRunningEventTo(Organization organization, RunningEvent runningEvent) {
        organization.getRunningEvents().add(runningEvent);
    }
}
