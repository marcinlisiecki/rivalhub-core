package com.rivalhub.event.darts.match.result;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SinglePlayerScoreInRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserData userData;
    private Long score;
    private Long blanks;

}
