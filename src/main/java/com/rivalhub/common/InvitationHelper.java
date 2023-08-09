package com.rivalhub.common;

import com.rivalhub.organization.OrganizationDTO;

public class InvitationHelper {
    public static String createInvitationLink(OrganizationDTO organizationDTO){
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        String frontUrl = "http://localhost:4200";
        builder.append(frontUrl)
                .append("/organizations/")
                .append(organizationDTO.getId())
                .append("/invitation/")
                .append(organizationDTO.getInvitationHash());
        return builder.toString();
    }
}
