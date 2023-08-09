package com.rivalhub.event.pingpong;

import com.rivalhub.event.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class PingPongEvent extends Event {
    @OneToMany
    List<PingPongMatch> pingPongMatchList;
}
