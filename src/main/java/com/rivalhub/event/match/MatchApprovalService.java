package com.rivalhub.event.match;

import com.rivalhub.common.exception.UserNotFoundException;
import com.rivalhub.user.UserData;
import com.rivalhub.user.notification.Notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchApprovalService {

    public static Map<Long, Boolean> prepareApprovalMap(MatchDto matchDto) {
        Map<Long,Boolean> userApproval = new HashMap<>();
        matchDto.getTeam1Ids().forEach(id-> userApproval.put(id,false));
        matchDto.getTeam2Ids().forEach(id-> userApproval.put(id,false));
        return userApproval;
    }

    public static void findNotificationToDisActivate(List<UserData> team, Long matchId) {
        team.forEach(
                userData -> {
                    userData.getNotifications()
                            .stream().filter(
                                    notification -> notification.getMatchId().equals(matchId))
                            .findFirst()
                            .orElseThrow(UserNotFoundException::new)
                            .setStatus(Notification.Status.CONFIRMED);
                }
        );
    }
}
