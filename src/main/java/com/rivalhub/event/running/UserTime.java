package com.rivalhub.event.running;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserData user;
    private Double time;


}
