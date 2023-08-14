package com.rivalhub.organization.controller;


import com.rivalhub.event.EventType;
import com.rivalhub.organization.service.OrganizationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
public class OrganizationSettingsController {
    private final OrganizationSettingsService organizationSettingsService;


    @PostMapping("{organizationId}/admin/{userId}")
    private ResponseEntity<?> addAdmin(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long organizationId,
                                      @PathVariable Long userId){
        return ResponseEntity.ok(organizationSettingsService.setAdmin(userDetails.getUsername(), organizationId, userId));
    }

    @PostMapping("{organizationId}/admin")
    private ResponseEntity<?> changeInvitationLinkVisibility(@PathVariable Long organizationId,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestParam(name = "onlyAdminCanSeeInvitationLink", defaultValue = "true")
                                                     boolean onlyAdminCanSeeInvitationLink){
        return ResponseEntity.ok(organizationSettingsService.setOnlyAdminCanSeeInvitationLink(userDetails.getUsername(), organizationId,  onlyAdminCanSeeInvitationLink));
    }

    @DeleteMapping("{organizationId}/admin/event-types")
    private ResponseEntity<?> removeEventType(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long organizationId,
                                      @RequestParam("type") EventType eventType){
        return ResponseEntity.ok(organizationSettingsService.removeEventType(userDetails.getUsername(), organizationId, eventType));
    }

    @GetMapping("{organizationId}/event-types")
    private ResponseEntity<?> getEventTypesInOrganization(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long organizationId){
        return ResponseEntity.ok(organizationSettingsService.getEventTypesInOrganization(userDetails.getUsername(), organizationId));
    }

    @PostMapping("{organizationId}/admin/event-types")
    private ResponseEntity<?> addEventType(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable Long organizationId,
                                   @RequestParam("type") EventType eventType){
        return ResponseEntity.ok(organizationSettingsService.addEventType(userDetails.getUsername(), organizationId, eventType));
    }

    @GetMapping("/event-types")
    private ResponseEntity<?> allEventTypeInApp(){
        return ResponseEntity.ok(organizationSettingsService.allEventTypeInApp());
    }

    @GetMapping("/{id}/invitation")
    private ResponseEntity<?> viewInvitation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationSettingsService.viewInvitationLink(id, userDetails.getUsername()));
    }

    @GetMapping("/{id}/settings")
    private ResponseEntity<?> showSettings(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationSettingsService.showSettings(id, userDetails.getUsername()));
    }
}
