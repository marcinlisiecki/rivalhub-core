package com.rivalhub.event.pingpong;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.EventDto;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.ReservationDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PingPongEventSaver {
    private final RepositoryManager repositoryManager;
    private final OrganizationReservationService reservationService;
    private final PingPongEventRepository pingPongEventRepository;

    PingPongEvent saveEvent(PingPongEvent pingPongEvent, Organization organization, EventDto eventDto) {
        pingPongEvent.setOrganization(organization);

        for (Long id : eventDto.getParticipants()) {
            pingPongEvent.getParticipants().add(repositoryManager.findUserById(id));
        }
        pingPongEvent.setHost(repositoryManager.findUserById(eventDto.getHost()));

        AddReservationDTO addReservationDTO = setAddReservationDTO(eventDto, organization);

        ReservationDTO reservationDTO = reservationService.addReservation(addReservationDTO);

        pingPongEvent.setStartTime(LocalDateTime.parse(eventDto.getStartTime(), FormatterHelper.formatter()));
        pingPongEvent.setEndTime(LocalDateTime.parse(eventDto.getEndTime(), FormatterHelper.formatter()));

        pingPongEvent.setReservation(repositoryManager.findReservationById(reservationDTO.getId()));

        return pingPongEventRepository.save(pingPongEvent);
    }

    private static AddReservationDTO setAddReservationDTO(EventDto eventDto, Organization organization) {
        AddReservationDTO addReservationDTO = new AddReservationDTO();
        addReservationDTO.setEndTime(eventDto.getEndTime());
        addReservationDTO.setStartTime(eventDto.getStartTime());
        addReservationDTO.setStationsIdList(eventDto.getStationList());
        addReservationDTO.setOrganizationId(organization.getId());
        return addReservationDTO;
    }
}
