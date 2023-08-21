package com.rivalhub.event.match;

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
public class MatchOperator {
    private final List<MatchServiceInterface> listOfImplementations;


    public MatchServiceInterface useStrategy(String strategy) {
        return listOfImplementations.stream()
                .filter(implementation -> implementation.matchStrategy(strategy))
                .findFirst()
                .orElseThrow(InvalidPathParamException::new);
    }


}
