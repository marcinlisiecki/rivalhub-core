package com.rivalhub.event.darts;

import com.rivalhub.event.Event;
import com.rivalhub.event.darts.match.DartMatch;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class DartEvent extends Event {
    List<DartMatch> dartsMatch;
}

