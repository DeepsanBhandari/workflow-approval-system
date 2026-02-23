package com.workflow.dto.response;

import java.time.LocalDateTime;

public record ApprovalHistoryResponse(
        Long id,
        UserResponse actor,
        String action,
        int level,
        String comments,
        String fromStatus,
        String toStatus,
        LocalDateTime createdAt
) {}
