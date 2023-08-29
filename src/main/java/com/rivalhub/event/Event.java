package com.rivalhub.event;

import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@MappedSuperclass
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String name;
    private String description;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endTime;

    @ManyToOne
    private UserData host;

    private Long reservationId;

    @ManyToMany
    private List<UserData> participants = new ArrayList<>();

    private boolean isEventPublic = false;

    public boolean isEventPublic() {
        return isEventPublic;
    }

    public void setIsEventPublic(boolean eventPublic) {
        isEventPublic = eventPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
