package com.rivalhub.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.organization.Organization;
import com.rivalhub.reservation.Reservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserData implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 256,message = ErrorMessages.NAME_DONT_FIT_SIZE)
    private String name;

    @Email(message = ErrorMessages.EMAIL_IS_NOT_VALID)
    private String email;

    private String profilePictureUrl;

    //@Length(min=8,message = ErrorMessages.PASSWORD_IS_TOO_SHORT)
    private String password;

    @ManyToMany(mappedBy = "userList", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Organization> organizationList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userData")
    @JsonBackReference
    private List<Reservation> reservationList = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserData(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                '}';
    }
}
