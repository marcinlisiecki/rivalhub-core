package com.rivalhub.event.pullups;

import com.rivalhub.event.Event;
import com.rivalhub.event.pullups.match.PullUpMatch;
import jakarta.persistence.Entity;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PullUpEvent extends Event {
    List<PullUpMatch> pullUpMatchList;
}
