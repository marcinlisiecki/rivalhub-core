package com.rivalhub.organization;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @NotNull
    @Size(min = 2, max = 256)
    private String name;


    @Size(min = 9, max = 10)
    private String invitationHash;


    @Size(min = 2, max = 512)
    private String imageUrl;

    private LocalDateTime addedDate;

    @ManyToMany
    @JsonManagedReference
    @JoinTable(name = "organization_users",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    private List<UserData> userList = new ArrayList<>();

    public Organization(String name, String invitationHash, String imageUrl) {
        this.name = name;
        this.invitationHash = invitationHash;
        this.imageUrl = imageUrl;
    }

    public void addUser(UserData userData){
        userData.getOrganizationList().add(this);
        userList.add(userData);
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
