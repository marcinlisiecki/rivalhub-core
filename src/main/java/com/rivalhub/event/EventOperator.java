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
    private final List<EventServiceInterface> listOfImplementations;


    public EventServiceInterface useStrategy(String strategy) {
        return listOfImplementations.stream()
                .filter(implementation -> implementation.matchStrategy(strategy))
                .findFirst()
                .orElseThrow(InvalidPathParamException::new);
    }


}
