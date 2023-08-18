package com.rivalhub.organization;

import com.rivalhub.event.EventType;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.darts.DartEvent;

import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.event.pullups.PullUpEvent;
import com.rivalhub.event.running.RunningEvent;
import com.rivalhub.event.tablefootball.TableFootballEvent;
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
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonManagedReference("user-organizations")
    @JoinTable(name = "organization_users",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private List<UserData> userList = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(
            name = "organization_station_list",
            joinColumns = @JoinColumn(
                    name = "organization_id",
                    referencedColumnName = "organization_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "station_list_id",
                    referencedColumnName = "id"
            )
    )
    private List<Station> stationList =  new ArrayList<>();

    @ElementCollection(targetClass = EventType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable
    private Set<EventType> eventTypeInOrganization = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UserData> adminUsers = new HashSet<>();

    private Boolean onlyAdminCanSeeInvitationLink = true;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<PingPongEvent> pingPongEvents = new ArrayList<>();

    private String colorForDefaultImage;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<BilliardsEvent> billiardsEvents = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<RunningEvent> runningEvents = new ArrayList<>();


    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<DartEvent> dartEvents = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<PullUpEvent> pullUpsEvents = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<TableFootballEvent> tableFootballEvents = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
