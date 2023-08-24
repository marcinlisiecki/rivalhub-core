package com.rivalhub.event.pullups.match.result;

import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

@Data
public class PullUpSeriesAddDto {
    private Long userId;
    private Long score;
    private Long seriesID;
}
