package com.rivalhub.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String color;
    public OrganizationDTO(String name, String colorForDefaultImage){
        this.name = name;
        this.color = colorForDefaultImage;
    }
}
