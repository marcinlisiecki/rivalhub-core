package com.rivalhub.event.match;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@MappedSuperclass
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private List<UserData> team1 = new ArrayList<>();
    @ManyToMany
    private List<UserData> team2 = new ArrayList<>();

    @ElementCollection
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();


}
