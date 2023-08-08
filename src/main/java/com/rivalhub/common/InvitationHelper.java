package com.rivalhub.common;

import com.rivalhub.organization.OrganizationDTO;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class InvitationHelper {
    public static String createInvitationLink(OrganizationDTO organizationDTO){
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
        uri.replacePath("");
        builder.append(uri.toUriString()).append("/")
                .append(organizationDTO.getId())
                .append("/invitation/")
                .append(organizationDTO.getInvitationHash());
        return builder.toString();
    }
}
