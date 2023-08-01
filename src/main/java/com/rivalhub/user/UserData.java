package com.rivalhub.user;

import com.rivalhub.organization.Organization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;
    private String email;
    private byte[] salt;
    private byte[] passwordHash;
    private String profilePictureUrl;

    @ManyToMany(mappedBy = "userList")
    private List<Organization> organizationList = new ArrayList<>();

    public UserData(String name) {
        this.name = name;
    }
}
