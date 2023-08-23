package com.rivalhub.user.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private ResponseEntity<?> updateUser(@RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return ResponseEntity.ok(profileService.updateProfile(patch));
    }
}
