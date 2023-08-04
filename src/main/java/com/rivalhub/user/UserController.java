package com.rivalhub.user;

import com.rivalhub.common.dto.ErrorMessageDto;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.email.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    UserService userService;
    EmailService emailService;

    public UserController(UserService userService, EmailService emailService){
        this.userService = userService;
        this.emailService = emailService;
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailsDto> getUserById(@PathVariable Long id){
        UserDetailsDto details = userService.findUserById(id);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserDto userDto){
        UserDto savedUser = userService.register(userDto);
        if(savedUser != null) {
            URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/users/{id}")
                    .buildAndExpand(savedUser.getId())
                    .toUri();
            emailService.sendSimpleMessage(savedUser.getEmail(), "Welcome on RivalHub","Your account was successfully created");
            return ResponseEntity.created(savedUserUri).body(savedUser);
        }
        return ResponseEntity.badRequest().body(new ErrorMessageDto("Adres email jest już w użyciu"));
    }

    @GetMapping("/users/organizations")
    public ResponseEntity<List<OrganizationCreateDTO>> listAllOrganizationsByUser(@AuthenticationPrincipal UserDetails userDetails){
        Optional<List<OrganizationCreateDTO>> userOrganizations = userService.findOrganizationsByUser(userDetails.getUsername());


        if (userOrganizations.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userOrganizations.get());
    }


}
