package com.rivalhub.event.pullups.match;

import com.rivalhub.event.EventType;
import com.rivalhub.event.pullups.match.result.PullUpSeries;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PullUpMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private List<UserData> participants;
    @OneToMany
    private List<PullUpSeries> pullUpSeries;


    private boolean approvalFirstPlace;
    private boolean approvalSecondPlace;
    private boolean approvalThirdPlace;
    @Transient
    private EventType eventType;
    @Transient
    private Long eventId;
}
