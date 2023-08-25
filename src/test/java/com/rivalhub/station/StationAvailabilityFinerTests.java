package com.rivalhub.station;

import com.rivalhub.common.FormatterHelper;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StationAvailabilityFinerTests {

    private List<Station> stationList;


    @BeforeEach
    void setup() {
        stationList = new ArrayList<>();

        Station station = new Station();
        station.setId(1L);
        station.setType(EventType.PING_PONG);
        station.setActive(true);
        Reservation reservation1 = new Reservation();

        reservation1.setStartTime(LocalDateTime.parse("24-08-2023 10:20", FormatterHelper.formatter()));
        reservation1.setEndTime(LocalDateTime.parse("24-08-2023 15:25", FormatterHelper.formatter()));


        station.setReservationList(List.of(reservation1));
        reservation1.setStationList(stationList);

        stationList.add(station);
    }

    @Test
    void find_active_stations_with_type_ping_pong() {
        // given
        Organization organization = new Organization();
        organization.setStationList(stationList);

        // when
        List<Station> activeStations = StationAvailabilityFinder.filterForActiveStationsAndTypeIn(organization, EventType.PING_PONG);

        // then
        assertThat(activeStations).isEqualTo(stationList);
    }

    @Test
    void find_available_stations_when_station_is_available() {
        // given
        Organization organization = new Organization();
        organization.setStationList(stationList);

        // when
        List<Station> availableStations = StationAvailabilityFinder
                .getAvailableStations(organization, "25-08-2023 10:30", "25-08-2023 11:30",
                        EventType.PING_PONG);

        // then
        assertThat(availableStations).isEqualTo(stationList);
    }

    @Test
    void find_available_stations_when_station_is_not_available() {
        // given
        Organization organization = new Organization();
        organization.setStationList(stationList);

        // when
        List<Station> availableStations = StationAvailabilityFinder
                .getAvailableStations(organization, "24-08-2023 10:30", "24-08-2023 20:30",
                        EventType.PING_PONG);

        // then
        assertThat(availableStations).isEmpty();
    }
}