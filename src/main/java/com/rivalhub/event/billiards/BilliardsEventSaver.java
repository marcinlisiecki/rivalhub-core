package com.rivalhub.event.billiards;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.billiards.match.BilliardsMatch;
import com.rivalhub.event.billiards.match.BilliardsMatchRepository;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.ReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BilliardsEventSaver {

    private final RepositoryManager repositoryManager;
    private final OrganizationReservationService reservationService;
    private final BilliardsEventRepository billiardsEventRepository;
    private final BilliardsMatchRepository billiardsMatchRepository;

    BilliardsEvent saveEvent(BilliardsEvent billiardsEvent, Organization organization, EventDto eventDto) {
        BilliardsMatch match= new BilliardsMatch();

        for (Long id : eventDto.getTeam1()) {
            match.getTeam1().add(repositoryManager.findUserById(id));
        }
        for (Long id : eventDto.getTeam2()) {
            match.getTeam2().add(repositoryManager.findUserById(id));
        }
        billiardsMatchRepository.save(match);
        billiardsEvent.getBilliardsMatches().add(match);
        billiardsEvent.setOrganization(organization);

        for (Long id : eventDto.getParticipants()) {
            billiardsEvent.getParticipants().add(repositoryManager.findUserById(id));
        }
        billiardsEvent.setHost(repositoryManager.findUserById(eventDto.getHost()));
        billiardsEvent.setName(eventDto.getName());
        billiardsEvent.setDescription(eventDto.getDescription());

        AddReservationDTO addReservationDTO = new AddReservationDTO();
        addReservationDTO.setEndTime(eventDto.getEndTime());
        addReservationDTO.setStartTime(eventDto.getStartTime());
        addReservationDTO.setStationsIdList(eventDto.getStationList());
        ReservationDTO reservationDTO = reservationService.addReservation(addReservationDTO, organization.getId(), billiardsEvent.getHost().getEmail());

        billiardsEvent.setStartTime(LocalDateTime.parse(eventDto.getStartTime(), FormatterHelper.formatter()));
        billiardsEvent.setEndTime(LocalDateTime.parse(eventDto.getEndTime(), FormatterHelper.formatter()));

        billiardsEvent.setReservation(repositoryManager.findReservationById(reservationDTO.getId()));

        return billiardsEventRepository.save(billiardsEvent);
    }
}
