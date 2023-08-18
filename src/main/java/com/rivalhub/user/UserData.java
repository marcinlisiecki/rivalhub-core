package com.rivalhub.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.organization.Organization;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import java.util.ArrayList;
import java.util.Objects;

@Entity
@Setter
@Getter
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
    @NotBlank
    private String name;
    @Email(message = ErrorMessages.EMAIL_IS_NOT_VALID)
    private String email;

    private LocalDateTime joinTime;

    private LocalDateTime activationTime;

    private String profilePictureUrl;

    private String activationHash;

    //@Length(min=8,message = ErrorMessages.PASSWORD_IS_TOO_SHORT)
    private String password;

//    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
//    @JsonBackReference
//    private List<Organization> organizationList = new ArrayList<>();

//    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "userData")
//    @JsonBackReference("user-reservations")
//    private List<Reservation> reservationList = new ArrayList<>();


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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData userData)) return false;
        return Objects.equals(id, userData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
