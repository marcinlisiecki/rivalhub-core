package com.rivalhub.event.pingpong;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.EventDto;
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
public class PingPongEventSaver {
    private final OrganizationReservationService reservationService;
    private final OrganizationRepository organizationRepository;

    PingPongEvent saveEvent(PingPongEvent pingPongEvent, Organization organization, EventDto eventDto) {
        //TODO Narazie można dodać tylko użytkowników z danej organizacji!
        List<UserData> participants =
                organization.getUserList()
                        .stream().filter(usersExistingInOrganization(eventDto))
                        .toList();

        pingPongEvent.getParticipants().addAll(participants);
        pingPongEvent.setHost(getHost(organization, eventDto.getHost()));

        AddReservationDTO addReservationDTO = createAddReservationDTO(eventDto, organization);

        Reservation reservation = reservationService.addReservationForEvent(addReservationDTO, organization);

        pingPongEvent.setStartTime(LocalDateTime.parse(eventDto.getStartTime(), FormatterHelper.formatter()));
        pingPongEvent.setEndTime(LocalDateTime.parse(eventDto.getEndTime(), FormatterHelper.formatter()));
        pingPongEvent.setReservation(reservation);

        addPingPongEventTo(organization, pingPongEvent);
        organizationRepository.save(organization);
        return pingPongEvent;
    }

    private Predicate<UserData> usersExistingInOrganization(EventDto eventDto) {
        return userData -> eventDto.getParticipants().contains(userData.getId());
    }

    private UserData getHost(Organization organization, Long host) {
        return organization.getUserList()
                .stream().filter(userData -> userData.getId().equals(host))
                .findFirst()
                .orElseThrow(UserAlreadyExistsException::new);
    }
    private void addPingPongEventTo(Organization organization, PingPongEvent pingPongEvent) {
        organization.getPingPongEvents().add(pingPongEvent);
    }

    private AddReservationDTO createAddReservationDTO(EventDto eventDto, Organization organization) {
        return AddReservationDTO.builder()
                .endTime(eventDto.getEndTime())
                .startTime(eventDto.getStartTime())
                .stationsIdList(eventDto.getStationList())
                .organizationId(organization.getId())
                .build();
    }
}
