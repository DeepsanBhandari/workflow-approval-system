package com.workflow.dto.request;

import com.workflow.enums.ApprovalAction;
import jakarta.validation.constraints.NotNull;

public record ApprovalActionRequest(
        @NotNull(message = "Action is required")
        ApprovalAction action,

        String comments
) {}
