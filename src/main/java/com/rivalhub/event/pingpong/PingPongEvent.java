package com.rivalhub.event.pingpong;

import com.rivalhub.event.Event;
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
public class PingPongEvent extends Event {
    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<PingPongMatch> pingPongMatchList;

    public List<Long> getParticipantsId(){
        List<Long> participantsId = new ArrayList<>();
        for (UserData userData:this.getParticipants()) {
            participantsId.add(userData.getId());
        }
        return participantsId;
    }

    public List<Long> getStationId(){
        List<Long> stationId = new ArrayList<>();
        for (Station station:this.getReservation().getStationList()) {
            stationId.add(station.getId());
        }
        return stationId;
    }

    public void addPingPongMatch(PingPongMatch pingPongMatch){
        this.pingPongMatchList.add(pingPongMatch);
    }
}
