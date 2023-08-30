package com.rivalhub.event.pullups.match.result;

import com.rivalhub.event.EventType;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ViewPullUpMatchDto implements ViewMatchDto {

    private Long id;
    private List<UserDetailsDto> userDetailsDtos;
    private List<PullUpSeriesDto> scores;
    private Map<Long,Integer> places;
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();
    private boolean isApproved;
    private EventType eventType;
    private Long eventId;

}
