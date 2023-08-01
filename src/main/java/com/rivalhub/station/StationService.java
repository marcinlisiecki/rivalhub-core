package com.rivalhub.station;

import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final NewStationDtoMapper newStationDtoMapper;

    public StationService(StationRepository stationRepository, NewStationDtoMapper newStationDtoMapper) {
        this.stationRepository = stationRepository;
        this.newStationDtoMapper = newStationDtoMapper;
    }

    NewStationDto addStation(NewStationDto newStationDto) {
        Station station = newStationDtoMapper.map(newStationDto);
        Station savedStation = stationRepository.save(station);
        return newStationDtoMapper.map(savedStation);
    }
}
