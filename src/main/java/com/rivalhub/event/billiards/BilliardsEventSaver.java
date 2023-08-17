package com.rivalhub.event.billiards;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventUtils;
import com.rivalhub.event.pingpong.PingPongEvent;
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
public class BilliardsEventSaver {

    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;

    BilliardsEvent saveEvent(BilliardsEvent billiardsEvent, Organization organization, EventDto eventDto) {
        //TODO Narazie można dodać tylko użytkowników z danej organizacji!
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
