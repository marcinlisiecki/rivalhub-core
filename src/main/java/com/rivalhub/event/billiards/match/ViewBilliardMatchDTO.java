package com.rivalhub.event.billiards.match;

import com.rivalhub.event.EventType;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ViewBilliardMatchDTO implements ViewMatchDto {

    private List<UserDetailsDto> team1;
    private List<UserDetailsDto> team2;
    private boolean team1PlaysFull;
    private boolean team1HadPottedFirst;
    private boolean team1Won;
    private boolean team2Won;
    private WinType winType;
    private int howManyBillsLeftTeam1;
    private int howManyBillsLeftTeam2;
    private EventType eventType;
    private Long eventId;
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();
    private boolean isApproved;
    private Long matchId;
}
