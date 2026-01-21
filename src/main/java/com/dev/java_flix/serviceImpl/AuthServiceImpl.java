package com.dev.java_flix.serviceImpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.java_flix.dao.UserRepository;
import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.EmailValidationResponse;
import com.dev.java_flix.dto.response.LoginResponse;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.entity.User;
import com.dev.java_flix.enums.Role;
import com.dev.java_flix.exception.AccountDeactivatedException;
import com.dev.java_flix.exception.BadCredentialsException;
import com.dev.java_flix.exception.EmailAlreadyExistsException;
import com.dev.java_flix.exception.EmailNotVerifiedException;
import com.dev.java_flix.exception.InvalidTokenException;
import com.dev.java_flix.security.JwtUtil;
import com.dev.java_flix.service.AuthService;
import com.dev.java_flix.service.EmailService;
import com.dev.java_flix.util.ServiceUtils;

import jakarta.validation.Valid;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final ServiceUtils serviceUtils;

    public AuthServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        JwtUtil jwtUtil,
        ServiceUtils serviceUtils
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public MessageResponse signup(@Valid UserRequest userRequest) {
        
        if(userRepository.existsByEmail(userRequest.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setActive(true);
        user.setEmailVerified(false);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);

        //validade de 24 horas
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);

        return new MessageResponse("Registration successful! Please check your email to verify your account");
    }

    @Override
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                            .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if(!user.isActive()){
            throw new AccountDeactivatedException("Your account has been deactivated. Please contact support for assistance.");
        }
        
        if(!user.isEmailVerified()){
            throw new EmailNotVerifiedException("Please verify your email address before loggin in. Check your inbox for the verification link.");
        }

        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        
        boolean exists = userRepository.existsByEmail(email);
        return new EmailValidationResponse(exists, !exists);
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        
        User user = userRepository.findByVerificationToken(token)
                        .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token."));

        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())){
            throw new InvalidTokenException("Verification link has expired. Please request a new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return new MessageResponse("Email verified successfully! you can now login.");
    }

    @Override
    public MessageResponse resendVerification(String email) {
        
        User user = serviceUtils.getUserByEmailOrThrow(email);

        String verificationToken = UUID.randomUUID().toString();

        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

        userRepository.save(user);
        emailService.sendVerificationEmail(email, verificationToken);
        
        return new MessageResponse("Verification email resend successfully! Please check your inbox.");
    }

}
