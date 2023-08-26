package com.rivalhub.event.pingpong.match;


import com.rivalhub.event.match.Match;
import com.rivalhub.event.pingpong.match.result.PingPongSet;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class PingPongMatch extends Match {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PingPongSet> sets = new ArrayList<>();

}
