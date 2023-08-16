package com.rivalhub.event;

import com.rivalhub.common.exception.InvalidPathParamException;
import com.rivalhub.event.pingpong.PingPongService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class EventOperator {
    private Map<String,EventServiceInterface> strategies;
    private final List<EventServiceInterface> listOfImplementations;


    @PostConstruct
    private void prepareEventOperator(){
        strategies = new HashMap<>();
        for (EventServiceInterface implementation: listOfImplementations) {
            strategies.put(implementation.getEventType().name(),implementation);
        }
    }

    public EventServiceInterface useStrategy(String strategy){
        EventServiceInterface strategyChosen = this.strategies.get(strategy);
        if(strategyChosen == null){
            throw new InvalidPathParamException();
        }
        return strategyChosen;

    }


}
