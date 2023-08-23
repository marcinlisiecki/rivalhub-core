package com.rivalhub.event.pullups.match.result;

import org.springframework.stereotype.Component;

@Component
public class PullUpResultMapper {

    PullUpSeries map(PullUpSeriesDto pullUpSeriesDto){
        PullUpSeries pullUpSeries = new PullUpSeries();
        pullUpSeries.setPullUpScoreList(pullUpSeriesDto.getPullUpScoreDtoList().stream().map(this::map).toList());
        return pullUpSeries;
    }
    PullUpScore map(PullUpScoreDto pullUpScoreDto){
        PullUpScore pullUpScore = new PullUpScore();
        pullUpScore.setScore(pullUpScore.getScore());
        return pullUpScore;
    }
}
