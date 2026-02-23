package com.workflow.controller;

import com.workflow.dto.request.ApprovalActionRequest;
import com.workflow.dto.request.CreateWorkflowRequest;
import com.workflow.dto.response.ApiResponse;
import com.workflow.dto.response.ApprovalHistoryResponse;
import com.workflow.dto.response.PageResponse;
import com.workflow.dto.response.WorkflowResponse;
import com.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkflowResponse>> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        WorkflowResponse response = workflowService.createWorkflow(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workflow created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkflowResponse>> getWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getWorkflowById(id)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<WorkflowResponse>>> getMyWorkflows(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(workflowService.getMyWorkflows(userDetails.getUsername(), pageable)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<WorkflowResponse>>> getAllWorkflows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(workflowService.getAllWorkflows(pageable)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<WorkflowResponse>>> getWorkflowsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(workflowService.getWorkflowsByStatus(status, pageable)));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<WorkflowResponse>> submitWorkflow(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Workflow submitted", workflowService.submitWorkflow(id, userDetails.getUsername())));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<WorkflowResponse>> processApproval(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalActionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        WorkflowResponse response = workflowService.processApproval(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Approval action processed", response));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<WorkflowResponse>> cancelWorkflow(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Workflow cancelled", workflowService.cancelWorkflow(id, userDetails.getUsername())));
    }

    @GetMapping("/pending-for-me")
    public ResponseEntity<ApiResponse<List<WorkflowResponse>>> getPendingForMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getPendingWorkflowsForApprover(userDetails.getUsername())));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<ApprovalHistoryResponse>>> getWorkflowHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(workflowService.getWorkflowHistory(id)));
    }
}
