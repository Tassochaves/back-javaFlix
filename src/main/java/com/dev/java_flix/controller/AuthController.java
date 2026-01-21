package com.dev.java_flix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.java_flix.dto.request.ChangePasswordRequest;
import com.dev.java_flix.dto.request.EmailRequest;
import com.dev.java_flix.dto.request.LoginRequest;
import com.dev.java_flix.dto.request.ResetPasswordRequest;
import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.EmailValidationResponse;
import com.dev.java_flix.dto.response.LoginResponse;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody UserRequest userRequest){

        return ResponseEntity.ok(authService.signup(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){

        LoginResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<EmailValidationResponse> validateEmail(@RequestParam String email){
        return ResponseEntity.ok(authService.validateEmail(email));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.resendVerification(emailRequest.getEmail()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(authService.forgotPassword(emailRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest changePasswordRequest){

        String email = authentication.getName();
        return ResponseEntity.ok(
            authService.changePassword(
                email,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword()
            ));
    }
}
