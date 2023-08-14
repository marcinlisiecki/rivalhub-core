package com.rivalhub.event.pingpong.match;

import lombok.Data;

import java.util.List;

@Data
public class AddPingPongMatchDTO {
    private Long id;
    private List<Long> team1Ids;
    private List<Long> team2Ids;
    private int team1Score;
    private int team2Score;
    boolean team1Approval;
    boolean team2Approval;
}
