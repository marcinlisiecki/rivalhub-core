package com.rivalhub.event.running;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserTimes {

    @Id
    private Long id;
    Long UserID;
    Double time;


}
