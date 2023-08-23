package com.rivalhub.event.pullups.match.result;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PullUpScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    UserData user;
}
