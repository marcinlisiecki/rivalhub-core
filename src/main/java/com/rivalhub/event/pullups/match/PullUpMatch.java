package com.rivalhub.event.pullups.match;

import com.rivalhub.event.pullups.match.result.PullUpSeries;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class PullUpMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private List<UserData> participants;
    @OneToMany
    private List<PullUpSeries> pullUpSeries;

    @ElementCollection
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();

}
