package com.rivalhub.organization;

import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEventRepository;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.*;
import com.rivalhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RepositoryManager {
    private final OrganizationRepository organizationRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
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

    public List<Long> getSharedOrganizationIds(Long requestUserId, Long viewedUserId){
        return userRepository.getSharedOrganizationsIds(requestUserId, viewedUserId)
                .stream().map(id -> id.get(0, Long.class)).toList();
    }

}
