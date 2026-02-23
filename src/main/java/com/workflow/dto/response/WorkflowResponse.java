package com.workflow.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record WorkflowResponse(
        Long id,
        String title,
        String description,
        String status,
        UserResponse createdBy,
        int currentLevel,
        int totalLevels,
        String metadata,
        List<ApprovalStepResponse> approvalSteps,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
