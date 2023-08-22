package com.rivalhub.event.billiards;


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
public class BilliardsEventSaver {

    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;

    BilliardsEvent saveEvent(BilliardsEvent billiardsEvent, Organization organization, EventDto eventDto) {
        AddReservationDTO addReservationDTO = EventUtils.createAddReservationDTO(eventDto, organization);
        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);
        EventUtils.setBasicInfo(billiardsEvent,organization,eventDto,reservation);
        addBilliardsEventTo(organization, billiardsEvent);
        organizationRepository.save(organization);
        return billiardsEvent;
    }

    private void addBilliardsEventTo(Organization organization, BilliardsEvent billiardsEvent) {
        organization.getBilliardsEvents().add(billiardsEvent);
    }


}
