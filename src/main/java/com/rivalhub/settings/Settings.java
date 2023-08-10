package com.rivalhub.settings;

import com.rivalhub.event.EventType;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(targetClass = EventType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable
    private Set<EventType> eventTypeInOrganization;

    @OneToMany
    private Set<UserData> adminUsers;
}
