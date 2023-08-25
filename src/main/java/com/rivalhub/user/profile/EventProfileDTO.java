package com.rivalhub.user.profile;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventProfileDTO {
    private Long eventId;
    private List<Long> stationList;
    private String startTime;
    private String endTime;
    private OrganizationDTO organization;
    private EventType eventType;
    private Long numberOfParticipants;
    private String name;
}
