package com.rivalhub.event.running;

import com.rivalhub.event.Event;
import com.rivalhub.event.EventType;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class RunningEvent extends Event {
    @OneToMany(orphanRemoval = true,cascade = CascadeType.REMOVE)
    private List<UserTime> userTimeList;
    private Double distance;

    private EventType eventType = EventType.RUNNING;


    public List<Long> getParticipantsId() {
        List<Long> participantsId = new ArrayList<>();
        for (UserData userData : this.getParticipants()) {
            participantsId.add(userData.getId());
        }
        return participantsId;
    }
}
