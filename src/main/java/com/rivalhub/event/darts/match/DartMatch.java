package com.rivalhub.event.darts.match;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DartMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private List<UserData> team1 = new ArrayList<>();
    @ManyToMany
    private List<UserData> team2 = new ArrayList<>();
    //      TODO jakie dane z meczu chcemy??????

    private boolean team1Approval;
    private boolean team2Approval;
}
