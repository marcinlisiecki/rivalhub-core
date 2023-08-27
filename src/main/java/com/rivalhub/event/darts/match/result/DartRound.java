package com.rivalhub.event.darts.match.result;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DartRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SinglePlayerScoreInRound> singlePlayerScoreInRoundsList = new ArrayList<>();
}
