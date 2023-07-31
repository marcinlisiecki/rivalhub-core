package com.rivalhub.organization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDTO {

    private Integer id;

    private String name;

    private String invitationLink;

    private String imageUrl;

    public OrganizationDTO(String name, String invitationLink, String imageUrl) {
        this.name = name;
        this.invitationLink = invitationLink;
        this.imageUrl = imageUrl;
    }
}
