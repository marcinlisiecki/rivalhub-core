package com.rivalhub.organization;

import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RepositoryManager {
    private final OrganizationRepository organizationRepository;
    private final ReservationRepository reservationRepository;
    private final PingPongEventRepository pingPongEventRepository;

    public Organization findOrganizationById(Long id){
        return organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public Set<Reservation> reservationsByOrganizationIdAndUserId(Long organizationId, Long userId){
        return reservationRepository.reservationsByOrganizationIdAndUserId(organizationId, userId);
    }

    public Set<PingPongEvent> eventsByOrganizationIdAndUserId(Long organizationId, Long userId) {
        return pingPongEventRepository.findAllByOrganizationIdAndUserId(organizationId, userId);
    }

}
