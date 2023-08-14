package com.rivalhub.event.pingpong.match;


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
    private List<UserData> team1;
    @ManyToMany
    private List<UserData> team2;
    private int team1Score;
    private int team2Score;
    private boolean team1Approval;
    private boolean team2Approval;
}
