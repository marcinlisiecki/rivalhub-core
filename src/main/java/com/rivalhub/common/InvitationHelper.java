package com.rivalhub.common;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InvitationHelper {
    @Value("${app.frontUrl}")
    private String url;
    public String createInvitationLink(Organization organization){
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        builder.append(url)
                .append("/organizations/")
                .append(organization.getId())
                .append("/invitation/")
                .append(organization.getInvitationHash());
        return builder.toString();
    }
}
