package com.rivalhub.user.profile;


import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.billiards.match.BilliardsMatchMapper;
import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.darts.match.DartMatchMapper;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pingpong.match.PingPongMatchMapper;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.pullups.match.PullUpMatchMapper;
import com.rivalhub.event.running.RunningEvent;
import com.rivalhub.event.running.RunningResultsMapper;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.event.tablefootball.match.TableFootballMatchMapper;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepoManager;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserProfileHelper {
    private final OrganizationRepoManager organizationRepoManager;
    private final AutoMapper autoMapper;
    private final PingPongMatchMapper pingPongMatchMapper;
    private final BilliardsMatchMapper billiardsMatchMapper;
    private final DartMatchMapper dartMatchMapper;
    private final PullUpMatchMapper pullUpMatchMapper;
    private final TableFootballMatchMapper tableFootballMatchMapper;
    private final RunningResultsMapper runningResultsMapper;

    Set<ReservationInProfileDTO> getReservationsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Long> sharedOrganizationIds = getSharedOrganizationList(loggedUser, viewedUser);

        List<Organization> sharedOrganizations = organizationRepoManager.findAllOrganizationsByIds(sharedOrganizationIds);
        Set<ReservationInProfileDTO> reservationDTOs = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            List<ReservationInProfileDTO> reservations = organizationRepoManager
                    .reservationsByOrganizationIdAndUserId(sharedOrganization.getId(), viewedUser.getId())
                    .stream().map(setReservationInProfileDTO(sharedOrganization))
                    .toList();

            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }

    Set<EventProfileDTO> getEventsInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Long> sharedOrganizationIds = getSharedOrganizationList(loggedUser, viewedUser);
        List<Organization> sharedOrganizations = organizationRepoManager.findAllOrganizationsByIds(sharedOrganizationIds);

        Set<EventProfileDTO> eventList = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            eventList.addAll(getAllEventsProfileDTOInOrganizationByUser(sharedOrganization, viewedUser));
        }

        return eventList;
    }

    Set<ViewMatchDto> getMatchesInSharedOrganizations(UserData loggedUser, UserData viewedUser) {
        List<Long> sharedOrganizationIds = getSharedOrganizationList(loggedUser, viewedUser);
        List<Organization> sharedOrganizations = organizationRepoManager.findAllOrganizationsByIds(sharedOrganizationIds);

        Set<ViewMatchDto> eventList = new HashSet<>();

        for (Organization sharedOrganization : sharedOrganizations) {
            eventList.addAll(getAllMatchesByUser(sharedOrganization, viewedUser));
        }

        return eventList;
    }

    public Set<EventDto> getEventsByOrganizationsAndDateForRequestUser(UserData requestUser, String date) {
        List<Long> organizationsIdsByUser = organizationRepoManager.getOrganizationsIdsByUser(requestUser.getId());
        List<Organization> userOrganizations = organizationRepoManager.findAllOrganizationsByIds(organizationsIdsByUser);

        LocalDateTime datePattern = LocalDateTime.parse(date, FormatterHelper.formatter());

        Set<Event> eventList = new HashSet<>();
        Set<EventDto> eventDtos = new HashSet<>();
        for (Organization sharedOrganization : userOrganizations) {
            eventList.addAll(getAllEventsInOrganizationByUser(sharedOrganization, requestUser));

            eventList = filterByDate(eventList, datePattern);

            eventDtos.addAll(eventList.stream()
                    .map(event -> {
                        EventDto eventDto = autoMapper.mapToEventDto(event);
                        eventDto.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
                        return eventDto;
                    })
                    .collect(Collectors.toSet())
            );
        }
        return eventDtos;
    }

    private <T extends Event> Set<T> filterByDate(Set<T> events, LocalDateTime date) {
        return events.stream()
                .filter(event -> {
                            return event.getStartTime().getYear() == date.getYear()
                                    && event.getStartTime().getMonth() == date.getMonth()
                                    ||
                                    event.getEndTime().getYear() == date.getYear()
                                            && event.getEndTime().getMonth() == date.getMonth();
                        }
                ).collect(Collectors.toSet());
    }

    public Set<ReservationInProfileDTO> getAllReservationsByRequestUserAndMonth(UserData requestUser, String date) {
        List<Long> organizationsIdsByUser = organizationRepoManager.getOrganizationsIdsByUser(requestUser.getId());
        List<Organization> userOrganizations = organizationRepoManager.findAllOrganizationsByIds(organizationsIdsByUser);

        LocalDateTime datePattern = LocalDateTime.parse(date, FormatterHelper.formatter());

        Set<ReservationInProfileDTO> reservationDTOs = new HashSet<>();

        for (Organization sharedOrganization : userOrganizations) {
            Set<ReservationInProfileDTO> reservations = organizationRepoManager
                    .reservationsByOrganizationIdAndUserIdFilterByDate(sharedOrganization.getId(), requestUser.getId(), datePattern)
                    .stream().map(setReservationInProfileDTO(sharedOrganization))
                    .collect(Collectors.toSet());

            reservationDTOs.addAll(reservations);
        }
        return reservationDTOs;
    }

    private Set<Event> getAllEventsInOrganizationByUser(Organization sharedOrganization, UserData viewedUser) {
        Set<Event> eventList = new HashSet<>();

        Set<PingPongEvent> pingPongEvents = organizationRepoManager.
                pingPongEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<BilliardsEvent> billiardsEvents = organizationRepoManager.
                billiardsEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<DartEvent> dartEvents = organizationRepoManager.
                dartEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());


        Set<PullUpEvent> pullUpEvents = organizationRepoManager.
                pullUpsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<RunningEvent> runningEvents = organizationRepoManager.
                runningEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<TableFootballEvent> tableFootballEvents = organizationRepoManager.
                tableFootballEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        eventList.addAll(pingPongEvents);
        eventList.addAll(billiardsEvents);
        eventList.addAll(dartEvents);
        eventList.addAll(pullUpEvents);
        eventList.addAll(runningEvents);
        eventList.addAll(tableFootballEvents);

        return eventList;
    }

    private Set<ViewMatchDto> getAllMatchesByUser(Organization sharedOrganization, UserData viewedUser) {
        Set<ViewMatchDto> eventList = new HashSet<>();

        Set<PingPongEvent> pingPongEvents = organizationRepoManager.
                pingPongEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<BilliardsEvent> billiardsEvents = organizationRepoManager.
                billiardsEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<DartEvent> dartEvents = organizationRepoManager.
                dartEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<PullUpEvent> pullUpEvents = organizationRepoManager.
                pullUpsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<RunningEvent> runningEvents = organizationRepoManager.
                runningEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        Set<TableFootballEvent> tableFootballEvents = organizationRepoManager.
                tableFootballEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());


        pingPongEvents
                .forEach(pingPongEvent -> {
                    pingPongEvent.getPingPongMatchList().forEach(pingPongMatch -> {
                        if (pingPongMatch.getTeam1().contains(viewedUser) || pingPongMatch.getTeam2().contains(viewedUser)) {
                            pingPongMatch.setEventId(pingPongEvent.getEventId());
                            pingPongMatch.setEventType(EventType.PING_PONG);
                            eventList.add(pingPongMatchMapper.map(pingPongMatch));
                        }

                    });
                });

        billiardsEvents
                .forEach(billiardsEvent -> {
                    billiardsEvent.getBilliardsMatches().forEach(billiardsMatch -> {
                        if (billiardsMatch.getTeam1().contains(viewedUser) || billiardsMatch.getTeam2().contains(viewedUser)) {
                            billiardsMatch.setEventId(billiardsEvent.getEventId());
                            billiardsMatch.setEventType(EventType.BILLIARDS);
                            eventList.add(billiardsMatchMapper.map(billiardsMatch));
                        }

                    });
                });

        dartEvents
                .forEach(dartEvent -> {
                    dartEvent.getDartsMatch().forEach(dartMatch -> {
                        if (dartMatch.getParticipants().contains(viewedUser)) {
                            dartMatch.setEventId(dartEvent.getEventId());
                            dartMatch.setEventType(EventType.DARTS);
                            eventList.add(dartMatchMapper.map(dartMatch));
                        }

                    });
                });

        pullUpEvents
                .forEach(pullUpEvent -> {
                    pullUpEvent.getPullUpMatchList().forEach(pullUpMatch -> {
                        if (pullUpMatch.getParticipants().contains(viewedUser)) {
                            pullUpMatch.setEventId(pullUpEvent.getEventId());
                            pullUpMatch.setEventType(EventType.PULL_UPS);
                            eventList.add(pullUpMatchMapper.map(pullUpMatch));
                        }

                    });
                });

        tableFootballEvents
                .forEach(tableFootballEvent -> {
                    tableFootballEvent.getTableFootballMatch().forEach(tableFootballMatch -> {
                        if (tableFootballMatch.getTeam1().contains(viewedUser) || tableFootballMatch.getTeam2().contains(viewedUser)) {
                            tableFootballMatch.setEventId(tableFootballEvent.getEventId());
                            tableFootballMatch.setEventType(EventType.TABLE_FOOTBALL);
                            eventList.add(tableFootballMatchMapper.map(tableFootballMatch));
                        }

                    });
                });

        runningEvents
                .forEach(runningEvent -> {
                    runningEvent.getUserTimeList().forEach(userTimes -> {
                        if (userTimes.getUser().equals(viewedUser)) {
                            userTimes.setEventId(runningEvent.getEventId());
                            userTimes.setEventType(EventType.RUNNING);
                            eventList.add(runningResultsMapper.map(userTimes,runningEvent));
                        }
                    });
                });

        return eventList;
    }
    private Function<PingPongEvent, EventProfileDTO> pingPongToEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }

    private Function<BilliardsEvent, EventProfileDTO> billiardToEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }

    private Function<DartEvent, EventProfileDTO> dartsToEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }

    private Function<PullUpEvent, EventProfileDTO> pullUpsToEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }

    private Function<RunningEvent, EventProfileDTO> runningToEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }

    private Function<TableFootballEvent, EventProfileDTO> tableFootballToEventProfileDTO(Organization sharedOrganization) {
        return event -> {
            EventProfileDTO eventProfileDTO = autoMapper.mapToEventProfileDTO(event);
            eventProfileDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            eventProfileDTO.setNumberOfParticipants((long) event.getParticipants().size());
            return eventProfileDTO;
        };
    }


    private Function<Reservation, ReservationInProfileDTO> setReservationInProfileDTO(Organization sharedOrganization) {
        return reservation -> {
            ReservationInProfileDTO reservationDTO = autoMapper.mapToShowReservationInProfileDTO(reservation);
            reservationDTO.setOrganization(autoMapper.mapToOrganizationDto(sharedOrganization));
            return reservationDTO;
        };
    }

    private List<Long> getSharedOrganizationList(UserData loggedUser, UserData viewedUser) {
        return organizationRepoManager.getSharedOrganizationIds(loggedUser.getId(), viewedUser.getId());
    }

    private Set<EventProfileDTO> getAllEventsProfileDTOInOrganizationByUser(Organization sharedOrganization, UserData viewedUser) {
        Set<EventProfileDTO> eventList = new HashSet<>();

        Set<PingPongEvent> pingPongEvents = organizationRepoManager.
                pingPongEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        List<EventProfileDTO> pingPongEventProfileDTOStream = pingPongEvents.stream()
                .map(pingPongToEventProfileDTO(sharedOrganization))
                .toList();

        Set<BilliardsEvent> billiardsEvents = organizationRepoManager.
                billiardsEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        List<EventProfileDTO> billiardsEventsProfileDTOStream = billiardsEvents.stream()
                .map(billiardToEventProfileDTO(sharedOrganization))
                .toList();

        Set<DartEvent> dartEvents = organizationRepoManager.
                dartEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        List<EventProfileDTO> dartEventsProfileDTOStream = dartEvents.stream()
                .map(dartsToEventProfileDTO(sharedOrganization))
                .toList();

        Set<PullUpEvent> pullUpEvents = organizationRepoManager.
                pullUpsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        List<EventProfileDTO> pullUpEventsProfileDTOStream = pullUpEvents.stream()
                .map(pullUpsToEventProfileDTO(sharedOrganization))
                .toList();

        Set<RunningEvent> runningEvents = organizationRepoManager.
                runningEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        List<EventProfileDTO> runningEventsProfileDTOStream = runningEvents.stream()
                .map(runningToEventProfileDTO(sharedOrganization))
                .toList();

        Set<TableFootballEvent> tableFootballEvents = organizationRepoManager.
                tableFootballEventsWithParticipantsByOrganizationIdAndUserId(sharedOrganization, viewedUser.getId());

        List<EventProfileDTO> tableFootballEventsProfileDTOStream = tableFootballEvents.stream()
                .map(tableFootballToEventProfileDTO(sharedOrganization))
                .toList();

        eventList.addAll(billiardsEventsProfileDTOStream);
        eventList.addAll(pingPongEventProfileDTOStream);
        eventList.addAll(dartEventsProfileDTOStream);
        eventList.addAll(pullUpEventsProfileDTOStream);
        eventList.addAll(runningEventsProfileDTOStream);
        eventList.addAll(tableFootballEventsProfileDTOStream);

        return eventList;
    }

}
