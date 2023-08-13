package com.rivalhub.event.pingpong.match;

import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class PingPongMatchDTO {
    private Long id;
    private List<UserDetailsDto> team1;
    private List<UserDetailsDto> team2;
    private int team1Score;
    private int team2Score;
    boolean team1Approval;
    boolean team2Approval;
}
