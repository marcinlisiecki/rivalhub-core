package com.rivalhub.organization;

import com.rivalhub.reservation.Reservation;
import com.rivalhub.station.Station;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.engine.internal.Cascade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Size(min = 2, max = 256, message = ErrorMessages.NAME_SIZE)
    private String name;

    private String invitationHash;

    @Size(min = 2, max = 512)
    private String imageUrl;

    private LocalDateTime addedDate;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonManagedReference
    @JoinTable(name = "organization_users",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private List<UserData> userList = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    private List<Station> stationList;

    public Organization(String name, String invitationHash, String imageUrl) {
        this.name = name;
        this.invitationHash = invitationHash;
        this.imageUrl = imageUrl;
    }

    public void addUser(UserData userData){
        userData.getOrganizationList().add(this);
        userList.add(userData);
    }

    public void addStation(Station station){
        this.stationList.add(station);
    }

    public void removeStation(Station station){
        this.stationList.remove(station);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name + '\'' +
                ", invitationLink='" + invitationHash + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
