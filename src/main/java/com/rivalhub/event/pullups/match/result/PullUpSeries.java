package com.rivalhub.event.pullups.match.result;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PullUpSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany
    List<PullUpScore> pullUpScoreList;
}
