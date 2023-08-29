package com.rivalhub.event.tablefootball.match;

import com.rivalhub.event.EventType;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import com.rivalhub.user.UserDetailsDto;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.List;

@Data
public class ViewTableFootballMatchDTO implements ViewMatchDto {

    private Long id;
    private List<UserDetailsDto> team1;
    private List<UserDetailsDto> team2;
    private List<TableFootballMatchSet> sets;
    boolean team1Approval;
    boolean team2Approval;

    private EventType eventType;
    private Long eventId;
}
