package com.dev.java_flix.service;

import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.MessageResponse;

public interface UserService {

    MessageResponse createUser(UserRequest userRequest);

}
