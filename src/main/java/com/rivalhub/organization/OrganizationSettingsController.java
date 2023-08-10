package com.rivalhub.organization;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
public class OrganizationSettingsController {
    private final OrganizationSettingsService organizationSettingsService;


    @GetMapping("{organizationId}/admin/{userId}")
    public ResponseEntity<?> addAdmin(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long organizationId,
                                      @PathVariable Long userId){
        return ResponseEntity.ok(organizationSettingsService.setAdmin(userDetails.getUsername(), organizationId, userId));
    }

}
