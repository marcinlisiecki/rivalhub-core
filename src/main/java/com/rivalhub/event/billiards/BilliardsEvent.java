package com.rivalhub.event.billiards;

import com.rivalhub.event.Event;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.match.BilliardsMatch;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class BilliardsEvent extends Event {
    @OneToMany
    private List<BilliardsMatch> billiardsMatches = new ArrayList<>();

    private EventType eventType = EventType.BILLIARDS;
}
