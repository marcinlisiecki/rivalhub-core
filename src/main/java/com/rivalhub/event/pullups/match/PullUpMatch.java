package com.rivalhub.event.pullups.match;

import com.rivalhub.event.pullups.match.result.PullUpSeries;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PullUpMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    List<UserData> participants;
    @OneToMany
    List<PullUpSeries> pullUpSeries;
}
