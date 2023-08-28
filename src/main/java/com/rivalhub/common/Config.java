package com.rivalhub.common;


import com.rivalhub.event.EventDto;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.darts.DartEvent;
import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.running.RunningEvent;
import com.rivalhub.event.tablefootball.TableFootballEvent;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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

        modelMapper.typeMap(EventDto.class, BilliardsEvent.class).addMappings(mapper ->
                mapper.skip(BilliardsEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, BilliardsEvent.class).addMappings(mapper ->
                mapper.skip(BilliardsEvent::setHost));

        modelMapper.typeMap(BilliardsEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(BilliardsEvent::getParticipantsId,
                        EventDto::setParticipants));
        modelMapper.typeMap(BilliardsEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(src -> src.getHost().getId(),
                        EventDto::setHost));
        modelMapper.typeMap(BilliardsEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(BilliardsEvent::getStationId,EventDto::setStationList));



        modelMapper.typeMap(EventDto.class, DartEvent.class).addMappings(mapper ->
                mapper.skip(DartEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, DartEvent.class).addMappings(mapper ->
                mapper.skip(DartEvent::setHost));

        modelMapper.typeMap(DartEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(DartEvent::getParticipantsId,
                        EventDto::setParticipants));
        modelMapper.typeMap(DartEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(src -> src.getHost().getId(),
                        EventDto::setHost));
        modelMapper.typeMap(DartEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(DartEvent::getStationId,EventDto::setStationList));




        modelMapper.typeMap(EventDto.class, PullUpEvent.class).addMappings(mapper ->
                mapper.skip(PullUpEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, PullUpEvent.class).addMappings(mapper ->
                mapper.skip(PullUpEvent::setHost));

        modelMapper.typeMap(PullUpEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(PullUpEvent::getParticipantsId,
                        EventDto::setParticipants));
        modelMapper.typeMap(PullUpEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(src -> src.getHost().getId(),
                        EventDto::setHost));
        modelMapper.typeMap(PullUpEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(PullUpEvent::getStationId,EventDto::setStationList));

        modelMapper.typeMap(EventDto.class, TableFootballEvent.class).addMappings(mapper ->
                mapper.skip(TableFootballEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, TableFootballEvent.class).addMappings(mapper ->
                mapper.skip(TableFootballEvent::setHost));

        modelMapper.typeMap(TableFootballEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(TableFootballEvent::getParticipantsId,
                        EventDto::setParticipants));
        modelMapper.typeMap(TableFootballEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(src -> src.getHost().getId(),
                        EventDto::setHost));
        modelMapper.typeMap(TableFootballEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(TableFootballEvent::getStationId,EventDto::setStationList));

        modelMapper.typeMap(EventDto.class, RunningEvent.class).addMappings(mapper ->
                mapper.skip(RunningEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, RunningEvent.class).addMappings(mapper ->
                mapper.skip(RunningEvent::setHost));
        modelMapper.typeMap(EventDto.class, RunningEvent.class).addMappings(mapper ->
                mapper.map(EventDto::getDistance,RunningEvent::setDistance));

        modelMapper.typeMap(RunningEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(RunningEvent::getParticipantsId,
                        EventDto::setParticipants));
        modelMapper.typeMap(RunningEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(src -> src.getHost().getId(),
                        EventDto::setHost));
        modelMapper.typeMap(RunningEvent.class, EventDto.class).addMappings(mapper ->
                mapper.map(RunningEvent::getStationId,EventDto::setStationList));



        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setParticipants));
        modelMapper.typeMap(EventDto.class, PingPongEvent.class).addMappings(mapper ->
                mapper.skip(PingPongEvent::setHost));




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
