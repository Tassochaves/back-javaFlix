package com.dev.java_flix.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.java_flix.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
