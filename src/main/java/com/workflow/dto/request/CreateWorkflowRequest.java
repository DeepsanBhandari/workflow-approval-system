package com.workflow.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateWorkflowRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        String metadata,

        @NotEmpty(message = "At least one approval step is required")
        @Valid
        List<ApprovalStepRequest> approvalSteps
) {
    public record ApprovalStepRequest(
            @NotNull(message = "Approver ID is required")
            Long approverId,

            @Positive(message = "Level must be positive")
            int level,

            String stepName
    ) {}
}
