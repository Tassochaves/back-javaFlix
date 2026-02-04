package com.dev.java_flix.service;

import com.dev.java_flix.dto.request.UserRequest;
import com.dev.java_flix.dto.response.MessageResponse;
import com.dev.java_flix.dto.response.PageResponse;
import com.dev.java_flix.dto.response.UserResponse;

public interface UserService {

    MessageResponse createUser(UserRequest userRequest);

    MessageResponse updateUser(Long id, UserRequest userRequest);

    PageResponse<UserResponse> getUsers(int page, int size, String search);

    MessageResponse deleteUser(Long id, String currentUserEmail);

    MessageResponse toggleUserStatus(Long id, String currentUserEmail);

    MessageResponse changeUserRole(Long id, UserRequest userRequest);

}
