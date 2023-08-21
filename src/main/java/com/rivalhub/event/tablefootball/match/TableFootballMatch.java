package com.rivalhub.event.tablefootball.match;

import com.rivalhub.event.match.Match;
import com.rivalhub.event.tablefootball.match.result.TableFootballMatchSet;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class TableFootballMatch extends Match {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TableFootballMatchSet> sets = new ArrayList<>();

}
