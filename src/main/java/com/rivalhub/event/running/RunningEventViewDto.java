package com.rivalhub.event.running;

import com.rivalhub.event.EventDto;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class RunningEventViewDto  {
    private List<UserDetailsDto> userDetailsDtos;
    private List<UserTimesViewDto> userTimesViewDtoList;

}
