package com.rivalhub.event.pingpong.match;


import com.rivalhub.event.match.Match;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PingPongMatch extends Match {

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL)
    private List<PingPongSet> sets = new ArrayList<>();

}
