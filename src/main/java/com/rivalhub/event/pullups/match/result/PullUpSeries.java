package com.rivalhub.event.pullups.match.result;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PullUpSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<PullUpScore> pullUpScoreList;
}
