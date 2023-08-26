package com.rivalhub.event.match;

import java.util.HashMap;
import java.util.Map;

public class MatchApprovalService {

    public static Map<Long, Boolean> prepareApprovalMap(MatchDto matchDto) {
        Map<Long,Boolean> userApproval = new HashMap<>();
        matchDto.getTeam1Ids().forEach(id-> userApproval.put(id,false));
        matchDto.getTeam2Ids().forEach(id-> userApproval.put(id,false));
        return userApproval;
    }
}
