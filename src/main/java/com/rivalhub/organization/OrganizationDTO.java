package com.rivalhub.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String colorForDefaultImage;
    public OrganizationDTO(String name, String colorForDefaultImage){
        this.name = name;
        this.colorForDefaultImage = colorForDefaultImage;
    }
}
