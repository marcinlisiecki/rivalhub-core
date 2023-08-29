package com.rivalhub.event.running;

import com.rivalhub.common.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class RunningResultsMapper {
    public UserTimesViewDto map(UserTime userTime, RunningEvent runningEvent){
        UserTimesViewDto userTimesViewDto = new UserTimesViewDto();
        userTimesViewDto.setUserId(userTime.getUser().getId());
        userTimesViewDto.setTime(userTime.getTime());
        userTimesViewDto.setMeanTime(
                (double) Math.round(((userTime.getTime()/runningEvent.getDistance())))
        );
        return userTimesViewDto;
    }

    public UserTime map(UserTimesAddDto userTimesAddDto,RunningEvent runningEvent){
        UserTime userTime = new UserTime();
        userTime.setUser(runningEvent.getParticipants()
                .stream()
                .filter(userData -> userData.getId() == userTimesAddDto.getUserId())
                .findFirst()
                .orElseThrow(UserNotFoundException::new));
        userTime.setTime(userTimesAddDto.getTime());
        return userTime;
    }
}
