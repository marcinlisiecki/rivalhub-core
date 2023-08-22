package com.rivalhub.event;

import com.rivalhub.common.exception.InvalidPathParamException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class EventOperator {
    private final List<EventService> listOfImplementations;


    public EventService useStrategy(String strategy) {
        return listOfImplementations.stream()
                .filter(implementation -> implementation.matchStrategy(strategy))
                .findFirst()
                .orElseThrow(InvalidPathParamException::new);
    }


}
