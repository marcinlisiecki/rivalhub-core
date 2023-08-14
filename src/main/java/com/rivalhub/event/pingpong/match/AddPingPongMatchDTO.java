package com.rivalhub.event.pingpong.match;

import lombok.Data;

import java.util.List;

@Data
public class AddPingPongMatchDTO {
    private Long id;
    private List<Long> team1Ids;
    private List<Long> team2Ids;
    boolean team1Approval;
    boolean team2Approval;
}
