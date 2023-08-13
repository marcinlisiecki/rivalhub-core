package com.rivalhub.user.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ShowProfileController {
    private final ProfileService profileService;

    @GetMapping("/users/{id}/reservations")
    private ResponseEntity<?> sharedOrganizationReservations(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(profileService.getSharedOrganizationReservations(id, userDetails.getUsername()));
    }
}
