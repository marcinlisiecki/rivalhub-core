package com.rivalhub.station;

import com.rivalhub.organization.OrganizationRepository;

import org.springframework.stereotype.Service;

@Service
public class NewStationDtoMapper {
    OrganizationRepository organizationRepository;

    public NewStationDtoMapper(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

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
        station.setId(newStationDto.getId());

        return station;
    }
}
