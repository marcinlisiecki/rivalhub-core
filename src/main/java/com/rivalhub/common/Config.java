package com.rivalhub.common;


import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.PingPong.PingPongEvent;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.user.UserData;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;


@Configuration
public class Config {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(PingPongEvent.class, EventDto.class).addMappings(mapper ->
            mapper.map(PingPongEvent::getParticipantsId,
                    EventDto::setParticipants));


        return modelMapper;
        ModelMapper modelMapper = new ModelMapper();


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
