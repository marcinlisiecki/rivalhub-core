package com.rivalhub.event.running;

import lombok.Data;

@Data
public class UserTimesViewDto {
    private Long userId;
    private Double time;
    private Double meanTime;
}
