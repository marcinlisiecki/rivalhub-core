package com.rivalhub.organization.controller;

import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.service.OrganizationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationUserController {
    private final OrganizationUserService organizationUserService;

    @GetMapping("{id}/users")
    private ResponseEntity<Page<?>> viewUsers(@PathVariable Long id,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(organizationUserService.findUsersByOrganization(id, page, size));
    }

    @GetMapping("/{id}/invitation/{hash}")
    private ResponseEntity<?> addUser(@PathVariable Long id, @PathVariable String hash){
        return ResponseEntity.ok(organizationUserService.addUser(id, hash));
    }

    @GetMapping("/{id}/invite/{emailUserToAdd}")
    private ResponseEntity<OrganizationDTO> addUserThroughEmail(@PathVariable Long id, @PathVariable String emailUserToAdd){
        return ResponseEntity.ok(organizationUserService.addUserThroughEmail(id, emailUserToAdd));
    }

    @GetMapping("/{id}/users/all")
    private ResponseEntity<?> viewAllUsers(@PathVariable Long id){
        return ResponseEntity.ok(organizationUserService.viewAllUsers(id));
    }

    @DeleteMapping("/{organizationId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteUserFromOrganization(@PathVariable Long organizationId, @PathVariable Long userId){
        organizationUserService.deleteUserFromOrganization(organizationId, userId);
    }
}
