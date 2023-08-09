package com.rivalhub.common;

import com.rivalhub.organization.OrganizationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InvitationHelper {
    @Value("${frontUrl.url}")
    private String url;
    public String createInvitationLink(OrganizationDTO organizationDTO){
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        builder.append(url)
                .append("/organizations/")
                .append(organizationDTO.getId())
                .append("/invitation/")
                .append(organizationDTO.getInvitationHash());
        return builder.toString();
    }
}
