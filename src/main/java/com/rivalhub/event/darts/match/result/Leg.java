package com.rivalhub.event.darts.match.result;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Leg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<DartRound> roundList = new ArrayList<>();



}
