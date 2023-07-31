package com.rivalhub.organization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 2, max = 256)
    private String name;


    @Size(min = 9, max = 10)
    private String invitationLink;


    @Size(min = 2, max = 512)
    private String imageUrl;

    private LocalDateTime addedDate;

    public Organization(String name, String invitationLink, String imageUrl) {
        this.name = name;
        this.invitationLink = invitationLink;
        this.imageUrl = imageUrl;
    }
}
