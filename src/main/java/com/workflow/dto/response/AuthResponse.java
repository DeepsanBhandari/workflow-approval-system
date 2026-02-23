package com.workflow.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String email,
        String role,
        Long userId
) {
    public static AuthResponse of(String token, String username, String email, String role, Long userId) {
        return new AuthResponse(token, "Bearer", username, email, role, userId);
    }
}
