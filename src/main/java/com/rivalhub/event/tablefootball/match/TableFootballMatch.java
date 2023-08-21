package com.rivalhub.event.tablefootball.match;

import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class TableFootballMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private List<UserData> team1 = new ArrayList<>();
    @ManyToMany
    private List<UserData> team2 = new ArrayList<>();
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TableFootballMatchSet> sets = new ArrayList<>();
    private boolean team1Approval;
    private boolean team2Approval;
}
