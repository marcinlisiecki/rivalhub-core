package com.rivalhub.common;


import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
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
//        modelMapper.typeMap(PingPongEvent.class, EventDto.class).addMappings(mapper ->
//                mapper.map(src -> src.getOrganization().getId(),
//                        EventDto::setOrganization));
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


        return modelMapper;
    }
}
