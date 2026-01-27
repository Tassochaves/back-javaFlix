package com.dev.java_flix.serviceImpl;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.java_flix.dao.UserRepository;
import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.entity.User;
import com.dev.java_flix.enums.Role;
import com.dev.java_flix.exception.EmailAlreadyExistsException;
import com.dev.java_flix.exception.InvalidRoleException;
import com.dev.java_flix.service.EmailService;
import com.dev.java_flix.service.UserService;
import com.dev.java_flix.util.ServiceUtils;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceUtils serviceUtils;
    private final EmailService emailService;

    public UserServiceImpl(
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder, 
        ServiceUtils serviceUtils,
        EmailService emailService){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.serviceUtils = serviceUtils;
        this.emailService = emailService;
    }

    @Override
    public MessageResponse createUser(UserRequest userRequest) {
        
        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        validateRole(userRequest.getRole());

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        user.setActive(true);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));

        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);

        return new MessageResponse("User created successfully.");
    }

    private void validateRole(String role) {
        
        if(Arrays.stream(Role.values()).noneMatch(r -> r.name().equalsIgnoreCase(role))){
            throw new InvalidRoleException("Invalid role: " + role);
        }
    }

    @Override
    public MessageResponse updateUser(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserByIdOrThrow(id);

        ensureNotLastActiveAdmin(user);
        validateRole(userRequest.getRole());

        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));

        userRepository.save(user);
        return new MessageResponse("User updated successfully.");
    }

    private void ensureNotLastActiveAdmin(User user){
        if (user.isActive() && user.getRole() == Role.ADMIN){
            long activeAdminCount = userRepository.countByRoleAndActive(Role.ADMIN, true);

            if (activeAdminCount <= 1){
                throw new RuntimeException("Cannot deactivate the last active admin user");
            }
        }
    }

}
