package com.rivalhub.user.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/users/events")
    private ResponseEntity<?> getAllEventsByRequestUserAndMonth(@RequestParam("date") String date){
        return ResponseEntity.ok(profileService.getAllEventsByRequestUserAndMonth(date));
    }

    @GetMapping("/users/reservations")
    private ResponseEntity<?> getAllReservationsByRequestUserAndMonth(@RequestParam("date") String date){
        return ResponseEntity.ok(profileService.getAllReservationsByRequestUserAndMonth(date));
    }

    @GetMapping("/users/notifications")
    private ResponseEntity<?> getNotifications(){
        return ResponseEntity.ok(profileService.getNotifications());
    }

    @PatchMapping("/users")
    private ResponseEntity<?> updateUser(@RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return ResponseEntity.ok(profileService.updateProfile(patch));
    }

    @PostMapping("/users/image")
    private ResponseEntity<?> updateImage(@RequestParam(name = "thumbnail", required = false)
                                              MultipartFile multipartFile){
        return ResponseEntity.ok(profileService.updateImage(multipartFile));
    }

    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteUser(){
        profileService.deleteProfile();
    }
}
