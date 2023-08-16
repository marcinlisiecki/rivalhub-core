package com.rivalhub.event;

import com.rivalhub.common.exception.InvalidPathParamException;
import com.rivalhub.event.pingpong.PingPongService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class EventOperator {
    private Map<String,EventServiceInterface> strategies;
    private final PingPongService pingPongService;


    public EventOperator(PingPongService pingPongService){
        this.pingPongService = pingPongService;
        this.strategies = new HashMap<>();
        this.strategies.put(EventType.PING_PONG.name(),pingPongService);
    }

    public EventServiceInterface useStrategy(String strategy){
        EventServiceInterface strategyChosen = this.strategies.get(strategy);
        if(strategyChosen == null){
            throw new InvalidPathParamException();
        }
        return strategyChosen;

    }


}
