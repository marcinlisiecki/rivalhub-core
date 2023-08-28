package com.rivalhub.event.tablefootball.match;

import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ViewTableFootballMatchDTO implements ViewMatchDto {

    private Long id;
    private List<UserDetailsDto> team1;
    private List<UserDetailsDto> team2;
    private List<TableFootballMatchSet> sets;
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();
    private boolean isApproved;
}
