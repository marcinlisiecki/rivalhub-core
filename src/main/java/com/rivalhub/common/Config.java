package com.rivalhub.common;


import com.rivalhub.event.EventDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Configuration
public class Config {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(PingPongEvent.class, EventDto.class).addMappings(mapper ->
            mapper.map(PingPongEvent::getParticipantsId,
                    EventDto::setParticipants));
        modelMapper.typeMap(PingPongEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(src -> src.getHost().getId(),
                        EventDto::setHost));
        modelMapper.typeMap(PingPongEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(PingPongEvent::getStationId,EventDto::setStationList));

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setOrganization));
        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setHost));
        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setReservation));
        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setStartTime));
        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setEndTime));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setAdminUsers));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setAddedDate));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setEventTypeInOrganization));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setUserList));

        modelMapper.typeMap(OrganizationDTO.class, Organization.class).addMappings(mapper ->
                mapper.skip(Organization::setStationList));
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return modelMapper;
    }
}
