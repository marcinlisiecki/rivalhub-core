package com.rivalhub.event.running;

import com.rivalhub.event.EventType;
import com.rivalhub.event.match.ViewMatchDto;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserData user;
    private Double time;

    @Transient
    private EventType eventType;
    @Transient
    private Long eventId;

}
