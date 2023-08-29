package com.rivalhub.event.running;

import com.rivalhub.event.match.ViewMatchDto;
import lombok.Data;

@Data
public class UserTimesViewDto implements ViewMatchDto {
    private Long userId;
    private Double time;
    private Double meanTime;
}
