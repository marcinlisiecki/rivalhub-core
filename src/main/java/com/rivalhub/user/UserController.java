package com.rivalhub.user;

import com.rivalhub.common.dto.ErrorMessageDto;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/users/{id}")
    private ResponseEntity<UserDetailsDto> getUserById(@PathVariable Long id){
        UserDetailsDto details = userService.findUserById(id);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/users/me")
    private ResponseEntity<UserDetailsDto> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMe(userDetails));
    }

    @PostMapping("/register")
    private ResponseEntity<?> register(@RequestBody UserDto userDto){
        UserDto savedUser = userService.register(userDto);
        URI savedUserUri = userService.sendEmail(savedUser);

        return ResponseEntity.created(savedUserUri).body(savedUser);
    }

    @GetMapping("/users/organizations")
    private ResponseEntity<?> listAllOrganizationsByUser(@AuthenticationPrincipal UserDetails userDetails){
        List<OrganizationCreateDTO> userOrganizations = userService.findOrganizationsByUser(userDetails.getUsername());
        return ResponseEntity.ok(userOrganizations);
    }

    @GetMapping("/confirm/{hash}")
    private ResponseEntity<?> confirmUserEmail(@PathVariable String hash){
        userService.confirmUserEmail(hash);
        return ResponseEntity.ok(null);
    }

}
