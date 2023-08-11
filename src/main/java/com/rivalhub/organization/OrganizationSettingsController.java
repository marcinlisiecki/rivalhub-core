package com.rivalhub.organization;


import com.rivalhub.event.EventType;
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


    @GetMapping("{organizationId}/admin/{userId}")
    ResponseEntity<?> addAdmin(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable Long organizationId,
                                      @PathVariable Long userId){
        return ResponseEntity.ok(organizationSettingsService.setAdmin(userDetails.getUsername(), organizationId, userId));
    }

    @GetMapping("{organizationId}/admin")
    ResponseEntity<?> changeInvitationLinkVisibility(@PathVariable Long organizationId,
                                                     @AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestParam(name = "onlyAdminCanSeeInvitationLink", defaultValue = "true")
                                                     boolean onlyAdminCanSeeInvitationLink){
        return ResponseEntity.ok(organizationSettingsService.setOnlyAdminCanSeeInvitationLink(userDetails.getUsername(), organizationId,  onlyAdminCanSeeInvitationLink));
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

    @PostMapping("{organizationId}/admin/events")
    ResponseEntity<?> addEventType(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable Long organizationId,
                                   @RequestParam("type") EventType eventType){
        return ResponseEntity.ok(organizationSettingsService.addEventType(userDetails.getUsername(), organizationId, eventType));
    }

    @GetMapping("/events")
    ResponseEntity<?> allEventTypeInApp(){
        return ResponseEntity.ok(organizationSettingsService.allEventTypeInApp());
    }

}
