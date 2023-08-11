package com.rivalhub.event.pingpong;


import com.rivalhub.user.UserData;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PingPongMatch {
    @Id
    private Long id;
    @OneToMany
    List<UserData> team1;
    @OneToMany
    List<UserData> team2;
    int team1Score;
    int team2Score;


}
