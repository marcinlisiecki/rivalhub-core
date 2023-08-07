package com.rivalhub.event.PingPong;


import com.rivalhub.event.Event;
import com.rivalhub.user.UserData;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PingPongMatch extends Event {
    @OneToMany
    List<UserData> team1;
    @OneToMany
    List<UserData> team2;
    int team1Score;
    int team2Score;
}
