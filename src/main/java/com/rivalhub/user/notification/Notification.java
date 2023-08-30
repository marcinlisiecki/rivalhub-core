package com.rivalhub.user.notification;

import com.rivalhub.event.EventType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long matchId;
    private EventType type;
    private Status status;
    private Long eventId;

    public Notification(Long eventId, Long matchId, EventType type, Status status) {
        this.eventId= eventId;
        this.matchId = matchId;
        this.type = type;
        this.status = status;
    }

    public Notification() {}

    public enum Status {
        NOT_CONFIRMED,
        CONFIRMED,
    }
}
