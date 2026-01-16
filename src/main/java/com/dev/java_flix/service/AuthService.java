package com.dev.java_flix.service;

import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.MessageResponse;

import jakarta.validation.Valid;


public interface AuthService {

    MessageResponse signup(@Valid UserRequest userRequest);

}
