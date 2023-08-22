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
        return url +
                "/organizations/" +
                organization.getId() +
                "/invitation/" +
                organization.getInvitationHash();
    }
}
