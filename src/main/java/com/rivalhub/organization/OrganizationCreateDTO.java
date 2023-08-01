package com.rivalhub.organization;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class OrganizationCreateDTO {
    private Long id;

    private String name;


    private String imageUrl;

    public OrganizationCreateDTO(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
