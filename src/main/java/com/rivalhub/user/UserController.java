package com.rivalhub.user;

import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.auth.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private ResponseEntity<JwtTokenDto> register(@RequestBody RegisterRequestDto registerRequestDto){
        JwtTokenDto token = userService.register(registerRequestDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/users/organizations")
    private ResponseEntity<?> listAllOrganizationsByUser(@AuthenticationPrincipal UserDetails userDetails){
        List<OrganizationDTO> userOrganizations = userService.findOrganizationsByUser(userDetails.getUsername());
        return ResponseEntity.ok(userOrganizations);
    }

    @GetMapping("/confirm/{hash}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void confirmUserEmail(@PathVariable String hash){
        userService.confirmUserEmail(hash);
    }
}
