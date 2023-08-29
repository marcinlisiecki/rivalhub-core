package com.rivalhub.event.match;

import com.rivalhub.event.EventType;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@MappedSuperclass
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private List<UserData> team1 = new ArrayList<>();
    @ManyToMany
    private List<UserData> team2 = new ArrayList<>();
    private boolean team1Approval;
    private boolean team2Approval;
    @Transient
    private EventType eventType;
    @Transient
    private Long eventId;
}
