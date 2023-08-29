package com.rivalhub.event.running;


import lombok.Data;

@Data
public class UserTimesAddDto {
    private Long userId;
    private Double time;
}
