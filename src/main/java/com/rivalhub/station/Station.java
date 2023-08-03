package com.rivalhub.station;

import com.rivalhub.common.ErrorMessages;
import com.rivalhub.organization.Organization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @NotNull(message = ErrorMessages.NAME_IS_REQUIRED)
    @Size(min = 2, max = 256, message = ErrorMessages.NAME_SIZE)
    private String name;

    @ManyToOne
    private Organization organization;

    public Station(Long id, String type, Organization organization) {
        this.id = id;
        this.type = type;
        this.organization = organization;
    }
}
