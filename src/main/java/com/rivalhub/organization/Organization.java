package com.rivalhub.organization;

import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.catalina.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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

    @NotNull
    @Size(min = 2, max = 256)
    private String name;


    @Size(min = 9, max = 10)
    private String invitationLink;


    @Size(min = 2, max = 512)
    private String imageUrl;

    private LocalDateTime addedDate;

    @ManyToMany
    @JoinTable(name = "organization_users",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private List<UserData> userList;

    @OneToMany
    private List<Station> stationList;

    public Organization(String name, String invitationLink, String imageUrl) {
        this.name = name;
        this.invitationLink = invitationLink;
        this.imageUrl = imageUrl;
    }

    public void addUser(UserData userData){
        userData.getOrganizationList().add(this);
        userList.add(userData);
    }

    public void addStation(Station station){
        this.stationList.add(station);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name + '\'' +
                ", invitationLink='" + invitationLink + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
