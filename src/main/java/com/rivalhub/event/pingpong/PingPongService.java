package com.rivalhub.event.pingpong;

import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PingPongService implements EventServiceInterface {

    final AutoMapper autoMapper;
    final PingPongEventRepository pingPongEventRepository;

    @Override
    public EventDto addEvent(EventDto eventDto) {
        PingPongEvent savedEvent = pingPongEventRepository.save(autoMapper.mapToPingPongEvent(eventDto));
        return autoMapper.mapToEventDto(savedEvent);
    }

}
