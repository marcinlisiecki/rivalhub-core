package com.rivalhub.event.pullups.match.result;

import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class ViewPullUpMatchDto {
    private List<UserDetailsDto> userDetailsDtos;
    private List<PullUpSeriesDto> pullUpSeriesDtos;
    private List<Integer> places;
}
