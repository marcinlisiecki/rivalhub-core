package com.rivalhub.event.pullups.match.result;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PullUpSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserData user;
    private Long score;
    private Long seriesID;
}
