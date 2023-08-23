package com.rivalhub.organization;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizationSettingsDTO {

    private Boolean onlyAdminCanSeeInvitationLink;
}
