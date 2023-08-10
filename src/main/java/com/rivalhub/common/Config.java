package com.rivalhub.common;


import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.user.UserData;
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
    }
}
