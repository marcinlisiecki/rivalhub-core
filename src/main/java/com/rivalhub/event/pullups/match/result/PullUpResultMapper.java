package com.rivalhub.event.pullups.match.result;

import com.rivalhub.common.exception.UserNotFoundException;
import com.rivalhub.event.pullups.match.PullUpMatch;
import com.rivalhub.user.UserData;
import com.rivalhub.user.profile.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PullUpResultMapper {


    public PullUpSeriesDto map(PullUpSeries pullUpSeries){
        PullUpSeriesDto pullUpSeriesDto = new PullUpSeriesDto();
        pullUpSeriesDto.setSeriesID(pullUpSeries.getSeriesID());
        pullUpSeriesDto.setScore((pullUpSeries.getScore()));
        pullUpSeriesDto.setUserId(pullUpSeries.getUser().getId());
        return  pullUpSeriesDto;
    }

    public PullUpSeries map(PullUpSeriesAddDto pullUpSeriesDto, PullUpMatch pullUpMatch){
        PullUpSeries pullUpSeries = new PullUpSeries();
        pullUpSeries.setSeriesID(pullUpSeriesDto.getSeriesID());
        pullUpSeries.setScore((pullUpSeriesDto.getScore()));
        pullUpSeries.setUser(pullUpMatch.getParticipants()
                .stream()
                .filter(user -> user.getId() == pullUpSeriesDto.getUserId())
                .findFirst()
                .orElseThrow(UserNotFoundException::new));
        return  pullUpSeries;
    }


}
