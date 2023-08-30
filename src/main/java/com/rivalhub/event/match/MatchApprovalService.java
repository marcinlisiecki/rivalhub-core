package com.rivalhub.event.match;

import com.rivalhub.common.exception.NotificationNotFoundException;
import com.rivalhub.common.exception.UserNotFoundException;
import com.rivalhub.event.EventType;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.notification.Notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchApprovalService {

    public static Map<Long, Boolean> prepareApprovalMap(MatchDto matchDto) {
        Map<Long,Boolean> userApproval = new HashMap<>();
        matchDto.getTeam1Ids().forEach(id-> userApproval.put(id,false));
        if (matchDto.getTeam2Ids() != null) matchDto.getTeam2Ids().forEach(id-> userApproval.put(id,false));
        return userApproval;
    }

    public static void findNotificationToDisActivate(List<UserData> team, Long matchId,EventType type,UserRepository userRepository) {
        team.forEach(
                userData -> {
                    userData.getNotifications()
                            .stream().filter(
                                    notification -> notification.getMatchId() == matchId && notification.getType() == type)
                            .findFirst()
                            .orElseThrow(NotificationNotFoundException::new)
                            .setStatus(Notification.Status.CONFIRMED);
                    userRepository.save(userData);

                }
        );
    }

    public static void saveNotification(UserData userData, EventType type, Long matchId, Long eventId, UserRepository userRepository) {
        if(userData.getNotifications().stream().noneMatch(notification -> (notification.getMatchId() == matchId && notification.getType() == type))) {
            userData.getNotifications().add(
                    new Notification(eventId, matchId, type, Notification.Status.NOT_CONFIRMED));
            userRepository.save(userData);
        }else {
            userData.getNotifications().stream()
                    .filter(notification -> (notification.getMatchId() == matchId && notification.getType() == type))
                    .findFirst()
                    .orElseThrow(NotificationNotFoundException::new).setStatus(Notification.Status.NOT_CONFIRMED);
        }
    }
}
