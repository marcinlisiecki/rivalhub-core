package com.rivalhub.event.pingpong;

import com.rivalhub.event.Event;
import com.rivalhub.user.UserData;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PingPongEvent extends Event {
    @OneToMany
    List<PingPongMatch> pingPongMatchList;

    public List<Long> getParticipantsId(){
        List<Long> participantsId = new ArrayList<>();
        for (UserData userData:this.getParticipants()) {
            participantsId.add(userData.getId());
        }
        return participantsId;
    }
}
