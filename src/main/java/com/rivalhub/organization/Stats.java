package com.rivalhub.organization;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserData userData;

    private Long winInPingPong = 0L;
    private Long gamesInPingPong = 0L;

    private Long winInBilliards = 0L;
    private Long gamesInBilliards = 0L;

    private Long winInDarts = 0L;
    private Long gamesInDarts = 0L;

    private Long winInPullUps = 0L;
    private Long gamesInPullUps = 0L;

    private Long winInRunning = 0L;
    private Long gamesInRunning = 0L;

    private Long winInTableFootBall = 0L;
    private Long gamesInTableFootBall = 0L;

    public Stats(UserData userData) {
        this.userData = userData;
    }

    public Stats() {}
}
