package com.rivalhub.event.darts.match;

import com.rivalhub.event.darts.match.result.Leg;
import com.rivalhub.event.darts.match.result.variables.DartFormat;
import com.rivalhub.event.darts.match.result.variables.DartMode;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DartMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @OneToMany(orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Leg> legList = new ArrayList<>();

    private DartFormat dartFormat;
    private DartMode dartMode;

    @ManyToMany
    private List<UserData> participants = new ArrayList<>();

    private boolean approvalFirstPlace;
    private boolean approvalSecondPlace;
    private boolean approvalThirdPlace;

}
