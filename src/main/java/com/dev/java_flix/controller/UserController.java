package com.dev.java_flix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.dto.response.PageResponse;
import com.dev.java_flix.dto.response.UserResponse;
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

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.updateUser(id, userRequest)); 
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search){

        return ResponseEntity.ok(userService.getUsers(page, size, search));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id, Authentication authentication){

        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(userService.deleteUser(id, currentUserEmail));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<MessageResponse> toggleUserStatus(@PathVariable Long id, Authentication authentication){

        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(userService.toggleUserStatus(id, currentUserEmail)); 
    }

    @PatchMapping("/{id}/change-role")
    public ResponseEntity<MessageResponse> changeUserRole(@PathVariable Long id, @RequestBody UserRequest userRequest){

        return ResponseEntity.ok(userService.changeUserRole(id, userRequest));
    }
}
