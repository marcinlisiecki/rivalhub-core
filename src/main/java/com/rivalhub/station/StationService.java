package com.rivalhub.station;

import com.rivalhub.organization.OrganizationNotFoundException;
import com.rivalhub.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final OrganizationRepository organizationRepository;
    private final NewStationDtoMapper newStationDtoMapper;

    NewStationDto addStation(NewStationDto newStationDto) {
        organizationRepository
                .findById(newStationDto.getOrganizationId())
                .orElseThrow(OrganizationNotFoundException::new);

        Station station = newStationDtoMapper.map(newStationDto);
        Station savedStation = stationRepository.save(station);
        return newStationDtoMapper.map(savedStation);
    }
}
