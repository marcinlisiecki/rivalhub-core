package com.rivalhub.event.tablefootball;

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
public class TableFootballEventSaver {
    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;


    TableFootballEvent saveEvent(TableFootballEvent tableFootballEvent, Organization organization, EventDto eventDto) {
        //TODO Narazie można dodać tylko użytkowników z danej organizacji!
        AddReservationDTO addReservationDTO = EventUtils.createAddReservationDTO(eventDto, organization);
        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);
        EventUtils.setBasicInfo(tableFootballEvent,organization,eventDto,reservation);
        addTableFootballEventTo(organization, tableFootballEvent);
        organizationRepository.save(organization);
        return tableFootballEvent;
    }



    private void addTableFootballEventTo(Organization organization, TableFootballEvent tableFootballEvent) {
        organization.getTableFootballEvents().add(tableFootballEvent);
    }

}
