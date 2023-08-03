package com.rivalhub.station;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;

import org.springframework.stereotype.Service;

@Service
public class NewStationDtoMapper {
    OrganizationRepository organizationRepository;

    public NewStationDtoMapper(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    NewStationDto map(Station station) {
        NewStationDto newStationDto = new NewStationDto();
        newStationDto.setType(station.getType());
        newStationDto.setName(station.getName());
        newStationDto.setOrganizationId(station.getOrganization().getId());
        return newStationDto;
    }

    Station map(NewStationDto newStationDto) {
        Station station = new Station();
        station.setType(newStationDto.getType());
        station.setName(newStationDto.getName());
        Organization organization = organizationRepository.findById(newStationDto.getOrganizationId()).orElseThrow();
        station.setOrganization(organization);

        return station;
    }
}
