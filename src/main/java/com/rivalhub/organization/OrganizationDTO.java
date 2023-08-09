package com.rivalhub.organization;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrganizationDTO {
    private Long id;
    private String name;
    private String invitationHash;
    private String imageUrl;
}
