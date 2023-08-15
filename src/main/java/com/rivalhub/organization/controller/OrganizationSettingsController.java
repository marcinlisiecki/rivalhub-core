package com.rivalhub.organization.controller;


import com.rivalhub.event.EventType;
import com.rivalhub.organization.service.OrganizationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private ResponseEntity<?> addAdmin(@PathVariable Long organizationId, @PathVariable Long userId){
        return ResponseEntity.ok(organizationSettingsService.setAdmin(organizationId, userId));
    }

    @PostMapping("{organizationId}/admin")
    private ResponseEntity<?> changeInvitationLinkVisibility(@PathVariable Long organizationId,
                                                     @RequestParam(name = "onlyAdminCanSeeInvitationLink", defaultValue = "true")
                                                     boolean onlyAdminCanSeeInvitationLink){
        return ResponseEntity.ok(organizationSettingsService.setOnlyAdminCanSeeInvitationLink(organizationId,  onlyAdminCanSeeInvitationLink));
    }

    @DeleteMapping("{organizationId}/admin/event-types")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void removeEventType(@PathVariable Long organizationId,
                                      @RequestParam("type") EventType eventType){
        organizationSettingsService.removeEventType(organizationId, eventType);
    }

    @GetMapping("{organizationId}/event-types")
    private ResponseEntity<?> getEventTypesInOrganization(@PathVariable Long organizationId){
        return ResponseEntity.ok(organizationSettingsService.getEventTypesInOrganization(organizationId));
    }

    @PostMapping("{organizationId}/admin/event-types")
    private ResponseEntity<?> addEventType(@PathVariable Long organizationId,
                                   @RequestParam("type") EventType eventType){
        return ResponseEntity.ok(organizationSettingsService.addEventType(organizationId, eventType));
    }

    @GetMapping("/event-types")
    private ResponseEntity<?> allEventTypeInApp(){
        return ResponseEntity.ok(organizationSettingsService.allEventTypeInApp());
    }

    @GetMapping("/{id}/invitation")
    private ResponseEntity<?> viewInvitation(@PathVariable Long id){
        return ResponseEntity.ok(organizationSettingsService.viewInvitationLink(id));
    }

    @GetMapping("/{id}/settings")
    private ResponseEntity<?> showSettings(@PathVariable Long id){
        return ResponseEntity.ok(organizationSettingsService.showSettings(id));
    }
}
