package com.rivalhub.organization;

import com.rivalhub.event.EventType;
import com.rivalhub.station.Station;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @NotNull(message = ErrorMessages.NAME_IS_REQUIRED)
    @NotBlank(message = ErrorMessages.NAME_IS_REQUIRED)
    @Size(min = 2, max = 256, message = ErrorMessages.NAME_SIZE)
    private String name;

    private String invitationHash;

    @Size(min = 2, max = 512)
    private String imageUrl;

    @CreationTimestamp
    private LocalDateTime addedDate;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonManagedReference
    @JoinTable(name = "organization_users",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private List<UserData> userList = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    @JoinTable(
            name = "ORGANIZATION_STATION_LIST",
            joinColumns = @JoinColumn(
                    name = "ORGANIZATION_ID",
                    referencedColumnName = "organization_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "STATION_LIST_ID",
                    referencedColumnName = "id"
            )
    )
    private List<Station> stationList;

    @ElementCollection(targetClass = EventType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable
    private Set<EventType> eventTypeInOrganization = new HashSet<>();

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<UserData> adminUsers = new HashSet<>();
}
