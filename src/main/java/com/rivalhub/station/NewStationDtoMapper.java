package com.rivalhub.station;

import com.rivalhub.organization.OrganizationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewStationDtoMapper {

    private final StationRepository stationRepository;

    public NewStationDto map(Station station) {
        NewStationDto newStationDto = new NewStationDto();
        newStationDto.setType(station.getType());
        newStationDto.setName(station.getName());
        newStationDto.setId(station.getId());
        return newStationDto;
    }

    public Station map(NewStationDto newStationDto) {
        Station station = new Station();
        station.setType(newStationDto.getType());
        station.setName(newStationDto.getName());

        return station;
    }

    public Station mapNewStationDtoToStation(NewStationDto newStationDto) {
        Station station = new Station();

        station.setType(newStationDto.getType());
        station.setName(newStationDto.getName());
        station.setId(newStationDto.getId());

        station.setReservationList(stationRepository
                .findById(newStationDto.getId())
                .orElseThrow(StationNotFoundException::new)
                .getReservationList());

        return station;
    }
}
