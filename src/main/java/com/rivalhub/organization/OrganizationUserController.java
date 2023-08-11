package com.rivalhub.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationUserController {
    private final OrganizationUserService organizationUserService;

    @GetMapping("{id}/users")
    ResponseEntity<Page<?>> viewUsers(@PathVariable Long id,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(organizationUserService.findUsersByOrganization(id, page, size));
    }

    @GetMapping("/{id}/invitation/{hash}")
    public ResponseEntity<?> addUser(@PathVariable Long id, @PathVariable String hash, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationUserService.addUser(id, hash, userDetails.getUsername()));
    }

    @GetMapping("/{id}/invite/{email}")
    public ResponseEntity<OrganizationDTO> addUserThroughEmail(@PathVariable Long id, @PathVariable String email){
        return ResponseEntity.ok(organizationUserService.addUserThroughEmail(id, email));
    }

    @GetMapping("/{id}/users/all")
    ResponseEntity<?> viewAllUsers(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationUserService.viewAllUsers(id, userDetails.getUsername()));
    }

}
