package com.rivalhub.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UserController {
    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserDtoDetails> getUserById(@PathVariable Long id){
        UserDtoDetails details = userService.findUserById(id);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/register")
    ResponseEntity<UserDto> register(@RequestBody UserDto userDto){
        UserDto savedUser = userService.register(userDto);
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/users/{id}")
                .buildAndExpand(userDto.getId())
                .toUri();
        return ResponseEntity.created(savedUserUri).body(userDto);
    }


}
