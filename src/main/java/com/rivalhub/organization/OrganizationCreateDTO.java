package com.rivalhub.organization;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCreateDTO {
    private Long id;
    private String name;
    private String imageUrl;
}
