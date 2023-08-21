package com.rivalhub.user.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/users/{id}/reservations")
    private ResponseEntity<?> sharedOrganizationReservations(@PathVariable Long id){
        return ResponseEntity.ok(profileService.getSharedOrganizationReservations(id));
    }

    @GetMapping("/users/{id}/events")
    private ResponseEntity<?> sharedOrganizationEvents(@PathVariable Long id){
        return ResponseEntity.ok(profileService.getSharedOrganizationEvents(id));
    }
}
