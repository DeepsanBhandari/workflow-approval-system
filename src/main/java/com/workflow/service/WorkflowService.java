package com.workflow.service;

import com.workflow.dto.request.ApprovalActionRequest;
import com.workflow.dto.request.CreateWorkflowRequest;
import com.workflow.dto.response.ApprovalHistoryResponse;
import com.workflow.dto.response.PageResponse;
import com.workflow.dto.response.WorkflowResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkflowService {
    WorkflowResponse createWorkflow(CreateWorkflowRequest request, String creatorUsername);
    WorkflowResponse getWorkflowById(Long id);
    PageResponse<WorkflowResponse> getMyWorkflows(String username, Pageable pageable);
    PageResponse<WorkflowResponse> getAllWorkflows(Pageable pageable);
    PageResponse<WorkflowResponse> getWorkflowsByStatus(String status, Pageable pageable);
    WorkflowResponse submitWorkflow(Long workflowId, String username);
    WorkflowResponse processApproval(Long workflowId, ApprovalActionRequest request, String approverUsername);
    WorkflowResponse cancelWorkflow(Long workflowId, String username);
    List<WorkflowResponse> getPendingWorkflowsForApprover(String approverUsername);
    List<ApprovalHistoryResponse> getWorkflowHistory(Long workflowId);
}
