package com.rivalhub.event.billiards;

import com.rivalhub.event.Event;
import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.match.BilliardsMatch;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
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


    //TODO do wywalenia po custom maperze
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
}
