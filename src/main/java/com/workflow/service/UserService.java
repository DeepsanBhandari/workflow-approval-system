package com.workflow.service;

import com.workflow.dto.request.RegisterRequest;
import com.workflow.dto.response.AuthResponse;
import com.workflow.dto.response.UserResponse;
import com.workflow.entity.User;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(String role);
    void deactivateUser(Long id);
    User findEntityByUsername(String username);
    User findEntityById(Long id);
}
