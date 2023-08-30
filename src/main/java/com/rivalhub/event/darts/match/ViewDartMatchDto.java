package com.rivalhub.event.darts.match;

import com.rivalhub.event.EventType;
import com.rivalhub.event.darts.match.result.variables.DartFormat;
import com.rivalhub.event.darts.match.result.variables.DartMode;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.user.UserDetailsDto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ViewDartMatchDto implements ViewMatchDto {


    private DartFormat dateFormat;
    private DartMode dartMode;
    private Long id;
    private List<UserDetailsDto> userDetails;
    private List<List<List<Long>>> scoresInMatch;
    private List<List<Integer>> pointsLeftInLeg;
    private List<List<Integer>> placesInLeg;
    private List<List<Integer>> bounceOutsInLeg;
    private List<List<Integer>> bestRoundScoresInLeg;
    private List<List<Integer>> numberOfRoundsPlayedInLeg;
    private List<List<List<Integer>>> pointsLeftInLegAfterRound;
    private List<List<List<Integer>>> bounceOutsInRound;
    private EventType eventType;
    private Long eventId;
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();
    private boolean isApproved;



}
