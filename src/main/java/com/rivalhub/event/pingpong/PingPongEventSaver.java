package com.rivalhub.event.pingpong;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventType;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.event.pingpong.match.PingPongMatchRepository;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.ReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PingPongEventSaver {
    private final RepositoryManager repositoryManager;
    private final OrganizationReservationService reservationService;
    private final PingPongEventRepository pingPongEventRepository;
    private final PingPongMatchRepository pingPongMatchRepository;
    PingPongEvent saveEvent(PingPongEvent pingPongEvent, Organization organization, EventDto eventDto) {
        PingPongMatch match= new PingPongMatch();

        for (Long id : eventDto.getTeam1()) {
            match.getTeam1().add(repositoryManager.findUserById(id));
        }
        for (Long id : eventDto.getTeam2()) {
            match.getTeam2().add(repositoryManager.findUserById(id));
        }
        pingPongMatchRepository.save(match);
        pingPongEvent.getPingPongMatchList().add(match);
        pingPongEvent.setOrganization(organization);

        for (Long id : eventDto.getParticipants()) {
            pingPongEvent.getParticipants().add(repositoryManager.findUserById(id));
        }
        pingPongEvent.setHost(repositoryManager.findUserById(eventDto.getHost()));
        pingPongEvent.setName(eventDto.getName());
        pingPongEvent.setDescription(eventDto.getDescription());

        AddReservationDTO addReservationDTO = new AddReservationDTO();
        addReservationDTO.setEndTime(eventDto.getEndTime());
        addReservationDTO.setStartTime(eventDto.getStartTime());
        addReservationDTO.setStationsIdList(eventDto.getStationList());
        ReservationDTO reservationDTO = reservationService.addReservation(addReservationDTO, organization.getId(), pingPongEvent.getHost().getEmail());

        pingPongEvent.setStartTime(LocalDateTime.parse(eventDto.getStartTime(), FormatterHelper.formatter()));
        pingPongEvent.setEndTime(LocalDateTime.parse(eventDto.getEndTime(), FormatterHelper.formatter()));

        pingPongEvent.setReservation(repositoryManager.findReservationById(reservationDTO.getId()));

        return pingPongEventRepository.save(pingPongEvent);
    }
}
