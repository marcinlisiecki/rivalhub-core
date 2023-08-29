package com.rivalhub.event.running;

import com.rivalhub.user.profile.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RunningEventMapper {
    private final RunningResultsMapper runningResultsMapper;

    public RunningEventViewDto map(RunningEvent runningEvent){
        RunningEventViewDto runningEventViewDto = new RunningEventViewDto();

        List<UserTimesViewDto> userTimesViewDtoList = new ArrayList<>();
        runningEventViewDto.setUserTimesViewDtoList(userTimesViewDtoList);
        runningEventViewDto.setUserDetailsDtos(runningEvent.getParticipants().stream().map(UserMapper::map).toList());
        return runningEventViewDto;
    }
}
