package com.rivalhub.event.PingPong;

import com.rivalhub.event.EventDto;
import com.rivalhub.event.EventServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.events.Event;

@RequiredArgsConstructor
@Service
public class PingPongService implements EventServiceInterface {

    final PingPongEventDtoMapper pingPongEventDtoMapper;
    final PingPongEventRepository pingPongEventRepository;

    @Override
    public EventDto addEvent(EventDto eventDto) {
        PingPongEvent savedEvent = pingPongEventRepository.save(pingPongEventDtoMapper.map(eventDto));
        return pingPongEventDtoMapper.map(savedEvent);
    }

}
