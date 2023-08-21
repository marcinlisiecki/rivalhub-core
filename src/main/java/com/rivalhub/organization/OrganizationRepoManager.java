package com.rivalhub.organization;

import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.exception.OrganizationNotFoundException;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationRepository;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrganizationRepoManager {
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ReservationRepository reservationRepository;


    public List<Organization> findAllOrganizationsByIds(List<Long> organizationIds){
        return (List<Organization>) organizationRepository.findAllById(organizationIds);
    }
    public Set<Reservation> reservationsByOrganizationIdAndUserId(Long organizationId, Long userId){
        return reservationRepository.reservationsByOrganizationIdAndUserId(organizationId, userId);
    }

    public List<Long> getSharedOrganizationIds(Long requestUserId, Long viewedUserId){
        return userRepository.getSharedOrganizationsIds(requestUserId, viewedUserId)
                .stream().map(id -> id.get(0, Long.class)).toList();
    }

    public List<Long> getOrganizationIdsWhereUserIsAdmin(UserData requestUser){
        return userRepository.getOrganizationIdsWhereUserIsAdmin(requestUser.getId())
                .stream().map(orgId -> orgId.get(0, Long.class)).toList();
    }

    public Organization getOrganizationWithStationsById(Long organizationId) {
        return entityManager.createQuery("""
                            select distinct o
                            from Organization o
                            join fetch o.stationList s
                            where o.id = :id
                        """, Organization.class)
                .setParameter("id", organizationId)
                .getResultStream()
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public Organization fetchReservationsFor(Organization organization) {
        List<Station> stationListWithReservations = entityManager.createQuery("""
                            select distinct s
                            from Station s
                            join fetch s.reservationList
                            where s in :stationsInOrganization
                        """, Station.class)
                .setParameter("stationsInOrganization", organization.getStationList())
                .getResultStream()
                .toList();

        organization.setStationList(stationListWithReservations);
        return organization;
    }
    public Organization getOrganizationWithUsersById(Long organizationId) {
        return entityManager.createQuery("""
                            select distinct o
                            from Organization o
                            join o.userList u
                            where o.id = :organizationId
                        """, Organization.class)
                .setParameter("organizationId", organizationId)
                .getResultStream()
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }
    private Organization pingPongEventsByOrganizationId(Long id) {
        return entityManager.createQuery("""
                select distinct o
                from Organization o
                join fetch o.pingPongEvents p
                where o.id = :id
                """, Organization.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public Set<PingPongEvent> eventsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
        List<PingPongEvent> pingPongEvents = entityManager.createQuery("""
                         select distinct e
                          from Organization o
                          join o.pingPongEvents e
                          where o = :organization
                        """, PingPongEvent.class)
                .setParameter("organization", organization)
                .getResultStream()
                .toList();

        List<PingPongEvent> pingPongEventsWithParticipants = entityManager.createQuery("""
                                                select distinct e
                                                from PingPongEvent e
                                                join e.participants p
                                                where p.id in :userId
                                                and e in :pingPongEvents
                        """, PingPongEvent.class)
                .setParameter("userId", userId)
                .setParameter("pingPongEvents", pingPongEvents)
                .getResultStream()
                .toList();;

        return new HashSet<>(pingPongEventsWithParticipants);
    }

    public Organization getOrganizationWithPingPongEventsById(Long id){
        Organization organization = pingPongEventsByOrganizationId(id);
        return fetchReservationsFor(organization);
    }

    public Organization getOrganizationWithStationsAndReservationsById(Long organizationId) {
        Organization organizationWithStationsById = getOrganizationWithStationsById(organizationId);
        return fetchReservationsFor(organizationWithStationsById);
    }
}
