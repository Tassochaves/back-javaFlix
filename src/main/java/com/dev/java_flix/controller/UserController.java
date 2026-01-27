package com.dev.java_flix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.service.UserService;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createUser(@RequestBody UserRequest userRequest){

        return ResponseEntity.ok(userService.createUser(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.updateUser(id, userRequest)); 
    }
}
