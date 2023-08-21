package com.rivalhub.event.pingpong;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventType;
import com.rivalhub.event.pingpong.match.PingPongMatch;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PingPongEvent extends Event {
    @OneToMany
    private List<PingPongMatch> pingPongMatchList = new ArrayList<>();

    private EventType eventType = EventType.PING_PONG;

    //TODO wywalić te metody do innej klasy jeżeli to możliwe
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
