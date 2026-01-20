package com.dev.java_flix.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.java_flix.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

}
