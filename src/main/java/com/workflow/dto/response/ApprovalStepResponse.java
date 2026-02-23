package com.workflow.dto.response;

import java.time.LocalDateTime;

public record ApprovalStepResponse(
        Long id,
        UserResponse approver,
        int level,
        String stepName,
        String status,
        String comments,
        LocalDateTime actedAt,
        LocalDateTime createdAt
) {}
