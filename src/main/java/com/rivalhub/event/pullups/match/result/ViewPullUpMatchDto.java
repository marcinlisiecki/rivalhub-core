package com.rivalhub.event.pullups.match.result;

import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class ViewPullUpMatchDto implements ViewMatchDto {
    private List<UserDetailsDto> userDetailsDtos;
    private List<List<Long>> scores;
    private List<Integer> places;

}
