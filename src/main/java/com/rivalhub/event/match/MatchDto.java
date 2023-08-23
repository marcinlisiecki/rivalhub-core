package com.rivalhub.event.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchDto {
    private Long id;
    private List<Long> team1Ids;
    private List<Long> team2Ids;
    private boolean team1Approval;
    private boolean team2Approval;
}
