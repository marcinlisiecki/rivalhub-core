package com.rivalhub.organization;


import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCreateDTO {
    private Long id;

    private String name;


    private String imageUrl;

    public OrganizationCreateDTO(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
