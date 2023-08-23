package com.rivalhub.event.pingpong.match;

import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class ViewPingPongMatchDTO implements ViewMatchDto {
    private Long id;
    private List<UserDetailsDto> team1;
    private List<UserDetailsDto> team2;
    private List<PingPongSet> sets;
    boolean team1Approval;
    boolean team2Approval;
}
