package com.rivalhub.user;

import com.rivalhub.auth.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/users/{id}")
    private ResponseEntity<UserDetailsDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/users/me")
    private ResponseEntity<UserDetailsDto> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PostMapping("/register")
    private ResponseEntity<JwtTokenDto> register(@RequestBody RegisterRequestDto registerRequestDto){
        return ResponseEntity.ok(userService.register(registerRequestDto));
    }

    @GetMapping("/users/organizations")
    private ResponseEntity<?> listAllOrganizationsByRequestUser(){
        return ResponseEntity.ok(userService.findOrganizationsByRequestUser());
    }

    @GetMapping("/confirm/{hash}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void confirmUserEmail(@PathVariable String hash){
        userService.confirmUserEmail(hash);
    }
}
