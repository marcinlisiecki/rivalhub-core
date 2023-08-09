package com.rivalhub.common;

import com.rivalhub.organization.OrganizationDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class InvitationHelper {
    public static String createInvitationLink(OrganizationDTO organizationDTO){
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
//        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
//        uri.replacePath("");
        String frontUrl = "http://localhost:4200";
        builder.append(frontUrl)
                .append("/organizations/")
                .append(organizationDTO.getId())
                .append("/invitation/")
                .append(organizationDTO.getInvitationHash());
        return builder.toString();
    }
}
