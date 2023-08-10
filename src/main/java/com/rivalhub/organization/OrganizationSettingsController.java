package com.rivalhub.organization;


import com.rivalhub.event.EventType;
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
    ResponseEntity<?> addAdmin(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long organizationId,
                                      @PathVariable Long userId){
        return ResponseEntity.ok(organizationSettingsService.setAdmin(userDetails.getUsername(), organizationId, userId));
    }

    @DeleteMapping("{organizationId}/admin/events")
    ResponseEntity<?> removeEventType(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long organizationId,
                                      @RequestParam("type") EventType eventType){
        return ResponseEntity.ok(organizationSettingsService.removeEventType(userDetails.getUsername(), organizationId, eventType));
    }

    @GetMapping("{organizationId}/events")
    ResponseEntity<?> getEventTypesInOrganization(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long organizationId){
        return ResponseEntity.ok(organizationSettingsService.getEventTypesInOrganization(userDetails.getUsername(), organizationId));
    }

}
