package com.rivalhub.organization;

import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.running.RunningEvent;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationRepository;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrganizationRepoManager {
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ReservationRepository reservationRepository;


    public List<Organization> findAllOrganizationsByIds(List<Long> organizationIds) {
        return (List<Organization>) organizationRepository.findAllById(organizationIds);
    }

    public Set<Reservation> reservationsByOrganizationIdAndUserId(Long organizationId, Long userId) {
        return reservationRepository.reservationsByOrganizationIdAndUserId(organizationId, userId);
    }

    public List<Long> getSharedOrganizationIds(Long requestUserId, Long viewedUserId) {
        return userRepository.getSharedOrganizationsIds(requestUserId, viewedUserId)
                .stream().map(id -> id.get(0, Long.class)).toList();
    }

    public List<Long> getOrganizationIdsWhereUserIsAdmin(UserData requestUser) {
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

    public Set<PingPongEvent> pingPongEventsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
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
                .toList();


        return new HashSet<>(pingPongEventsWithParticipants);
    }

    public Set<BilliardsEvent> billiardsEventsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
        List<BilliardsEvent> billiardsEvents = entityManager.createQuery("""
                         select distinct e
                          from Organization o
                          join o.billiardsEvents e
                          where o = :organization
                        """, BilliardsEvent.class)
                .setParameter("organization", organization)
                .getResultStream()
                .toList();

        List<BilliardsEvent> billiardsEventsWithParticipants = entityManager.createQuery("""
                                                select distinct e
                                                from BilliardsEvent e
                                                join e.participants p
                                                where p.id in :userId
                                                and e in :billiardsEvents
                        """, BilliardsEvent.class)
                .setParameter("userId", userId)
                .setParameter("billiardsEvents", billiardsEvents)
                .getResultStream()
                .toList();


        return new HashSet<>(billiardsEventsWithParticipants);
    }

    public Set<DartEvent> dartEventsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
        List<DartEvent> dartEvents = entityManager.createQuery("""
                         select distinct e
                          from Organization o
                          join o.dartEvents e
                          where o = :organization
                        """, DartEvent.class)
                .setParameter("organization", organization)
                .getResultStream()
                .toList();

        List<DartEvent> dartEventsWithParticipants = entityManager.createQuery("""
                                                select distinct e
                                                from DartEvent e
                                                join e.participants p
                                                where p.id in :userId
                                                and e in :dartEvents
                        """, DartEvent.class)
                .setParameter("userId", userId)
                .setParameter("dartEvents", dartEvents)
                .getResultStream()
                .toList();


        return new HashSet<>(dartEventsWithParticipants);
    }

    public Set<PullUpEvent> pullUpsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
        List<PullUpEvent> pullUpEvents = entityManager.createQuery("""
                         select distinct e
                          from Organization o
                          join o.pullUpsEvents e
                          where o = :organization
                        """, PullUpEvent.class)
                .setParameter("organization", organization)
                .getResultStream()
                .toList();

        List<PullUpEvent> pullUpEventsWithParticipants = entityManager.createQuery("""
                                                select distinct e
                                                from PullUpEvent e
                                                join e.participants p
                                                where p.id in :userId
                                                and e in :pullUpEvents
                        """, PullUpEvent.class)
                .setParameter("userId", userId)
                .setParameter("pullUpEvents", pullUpEvents)
                .getResultStream()
                .toList();

        return new HashSet<>(pullUpEventsWithParticipants);
    }

    public Set<RunningEvent> runningEventsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
        List<RunningEvent> runningEvents = entityManager.createQuery("""
                         select distinct e
                          from Organization o
                          join o.runningEvents e
                          where o = :organization
                        """, RunningEvent.class)
                .setParameter("organization", organization)
                .getResultStream()
                .toList();

        List<RunningEvent> runningEventsWithParticipants = entityManager.createQuery("""
                                                select distinct e
                                                from RunningEvent e
                                                join e.participants p
                                                where p.id in :userId
                                                and e in :runningEvents
                        """, RunningEvent.class)
                .setParameter("userId", userId)
                .setParameter("runningEvents", runningEvents)
                .getResultStream()
                .toList();

        return new HashSet<>(runningEventsWithParticipants);
    }

    public Set<TableFootballEvent> tableFootballEventsWithParticipantsByOrganizationIdAndUserId(Organization organization, Long userId) {
        List<TableFootballEvent> tableFootballEvents = entityManager.createQuery("""
                         select distinct e
                          from Organization o
                          join o.tableFootballEvents e
                          where o = :organization
                        """, TableFootballEvent.class)
                .setParameter("organization", organization)
                .getResultStream()
                .toList();

        List<TableFootballEvent> tableFootballEventsEventsWithParticipants = entityManager.createQuery("""
                                                select distinct e
                                                from TableFootballEvent e
                                                join e.participants p
                                                where p.id in :userId
                                                and e in :tableFootballEvents
                        """, TableFootballEvent.class)
                .setParameter("userId", userId)
                .setParameter("tableFootballEvents", tableFootballEvents)
                .getResultStream()
                .toList();

        return new HashSet<>(tableFootballEventsEventsWithParticipants);
    }

    public Organization getOrganizationWithPingPongEventsById(Long id) {
        Organization organization = pingPongEventsByOrganizationId(id);
        return fetchReservationsFor(organization);
    }

    public Organization getOrganizationWithStationsAndReservationsById(Long organizationId) {
        Organization organizationWithStationsById = getOrganizationWithStationsById(organizationId);
        return fetchReservationsFor(organizationWithStationsById);
    }

    public List<Long> getOrganizationsIdsByUser(Long id) {
        return userRepository.getOrganizationsByUserId(id)
                .stream().map(u -> u.get(0, Long.class))
                .toList();
    }

    public Set<PingPongEvent> eventsWithParticipantsByOrganizationIdAndUserIdFilteredByDate(Organization organization, Long userId, LocalDateTime date) {
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
                            where p.id = :userId
                            and e in :pingPongEvents
                        """, PingPongEvent.class)
                .setParameter("userId", userId)
                .setParameter("pingPongEvents", pingPongEvents)
                .getResultStream()
                .toList();



        return new HashSet<>(pingPongEventsWithParticipants);
    }

    public Set<Reservation> reservationsByOrganizationIdAndUserIdFilterByDate(Long organizationId, Long userId, LocalDateTime date) {
        Set<Reservation> reservations = reservationRepository.reservationsWithParticipantsByOrganizationIdAndUserIdWithFilterByDate(organizationId, userId);
        return reservations.stream()
                .filter(reservation -> {
                    return reservation.getStartTime().getYear() == date.getYear()
                            && reservation.getStartTime().getMonth() == date.getMonth()
                            ||
                            reservation.getEndTime().getYear() == date.getYear()
                                    && reservation.getEndTime().getMonth() == date.getMonth();
                }).collect(Collectors.toSet());
    }
}