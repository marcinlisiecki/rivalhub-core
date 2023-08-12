package com.rivalhub.event.pingpong;


import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PingPongMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    List<UserData> team1;
    @ManyToMany
    List<UserData> team2;
    int team1Score;
    int team2Score;
    boolean team1Approval;
    boolean team2Approval;
}
