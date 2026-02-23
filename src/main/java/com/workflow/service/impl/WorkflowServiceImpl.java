package com.workflow.service.impl;

import com.workflow.dto.request.ApprovalActionRequest;
import com.workflow.dto.request.CreateWorkflowRequest;
import com.workflow.dto.response.ApprovalHistoryResponse;
import com.workflow.dto.response.PageResponse;
import com.workflow.dto.response.WorkflowResponse;
import com.workflow.entity.ApprovalHistory;
import com.workflow.entity.ApprovalStep;
import com.workflow.entity.User;
import com.workflow.entity.Workflow;
import com.workflow.enums.ApprovalAction;
import com.workflow.enums.WorkflowStatus;
import com.workflow.exception.ResourceNotFoundException;
import com.workflow.exception.UnauthorizedException;
import com.workflow.exception.WorkflowException;
import com.workflow.mapper.WorkflowMapper;
import com.workflow.repository.ApprovalHistoryRepository;
import com.workflow.repository.ApprovalStepRepository;
import com.workflow.repository.WorkflowRepository;
import com.workflow.service.UserService;
import com.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final UserService userService;
    private final WorkflowMapper workflowMapper;

    @Override
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request, String creatorUsername) {
        User creator = userService.findEntityByUsername(creatorUsername);

        // Validate approval steps - levels must be sequential starting from 1
        List<CreateWorkflowRequest.ApprovalStepRequest> steps = request.approvalSteps()
                .stream()
                .sorted((a, b) -> Integer.compare(a.level(), b.level()))
                .toList();

        validateStepLevels(steps);

        Workflow workflow = Workflow.builder()
                .title(request.title())
                .description(request.description())
                .metadata(request.metadata())
                .createdBy(creator)
                .status(WorkflowStatus.DRAFT)
                .currentLevel(0)
                .totalLevels(steps.size())
                .build();

        Workflow saved = workflowRepository.save(workflow);

        for (CreateWorkflowRequest.ApprovalStepRequest stepRequest : steps) {
            User approver = userService.findEntityById(stepRequest.approverId());
            ApprovalStep step = ApprovalStep.builder()
                    .workflow(saved)
                    .approver(approver)
                    .level(stepRequest.level())
                    .stepName(stepRequest.stepName() != null ? stepRequest.stepName() : "Level " + stepRequest.level())
                    .status(WorkflowStatus.PENDING)
                    .build();
            approvalStepRepository.save(step);
        }

        log.info("Workflow created: id={}, title={}", saved.getId(), saved.getTitle());
        return workflowMapper.toResponse(workflowRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflowById(Long id) {
        return workflowMapper.toResponse(findWorkflowById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkflowResponse> getMyWorkflows(String username, Pageable pageable) {
        User user = userService.findEntityByUsername(username);
        Page<WorkflowResponse> page = workflowRepository.findByCreatedById(user.getId(), pageable)
                .map(workflowMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkflowResponse> getAllWorkflows(Pageable pageable) {
        Page<WorkflowResponse> page = workflowRepository.findAll(pageable)
                .map(workflowMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkflowResponse> getWorkflowsByStatus(String status, Pageable pageable) {
        WorkflowStatus workflowStatus = WorkflowStatus.valueOf(status.toUpperCase());
        Page<WorkflowResponse> page = workflowRepository.findByStatus(workflowStatus, pageable)
                .map(workflowMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public WorkflowResponse submitWorkflow(Long workflowId, String username) {
        Workflow workflow = findWorkflowById(workflowId);
        User user = userService.findEntityByUsername(username);

        if (!workflow.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the workflow creator can submit it");
        }

        if (workflow.getStatus() != WorkflowStatus.DRAFT && workflow.getStatus() != WorkflowStatus.CHANGES_REQUESTED) {
            throw new WorkflowException("Workflow can only be submitted from DRAFT or CHANGES_REQUESTED status");
        }

        String prevStatus = workflow.getStatus().name();
        workflow.setStatus(WorkflowStatus.IN_PROGRESS);
        workflow.setCurrentLevel(1);

        workflowRepository.save(workflow);

        recordHistory(workflow, user, ApprovalAction.APPROVE, 0,
                "Workflow submitted for approval", prevStatus, WorkflowStatus.IN_PROGRESS.name());

        log.info("Workflow submitted: id={}", workflowId);
        return workflowMapper.toResponse(workflowRepository.findById(workflowId).orElseThrow());
    }

    @Override
    public WorkflowResponse processApproval(Long workflowId, ApprovalActionRequest request, String approverUsername) {
        Workflow workflow = findWorkflowById(workflowId);
        User approver = userService.findEntityByUsername(approverUsername);

        if (workflow.getStatus() != WorkflowStatus.IN_PROGRESS) {
            throw new WorkflowException("Workflow is not in IN_PROGRESS state");
        }

        ApprovalStep currentStep = approvalStepRepository
                .findByWorkflowIdAndLevel(workflowId, workflow.getCurrentLevel())
                .orElseThrow(() -> new ResourceNotFoundException("Approval step not found for current level"));

        if (!currentStep.getApprover().getId().equals(approver.getId())) {
            throw new UnauthorizedException("You are not the approver for the current level");
        }

        String prevStatus = workflow.getStatus().name();
        currentStep.setComments(request.comments());
        currentStep.setActedAt(LocalDateTime.now());

        switch (request.action()) {
            case APPROVE -> handleApprove(workflow, currentStep);
            case REJECT -> handleReject(workflow, currentStep);
            case REQUEST_CHANGES -> handleRequestChanges(workflow, currentStep);
        }

        approvalStepRepository.save(currentStep);
        Workflow updatedWorkflow = workflowRepository.save(workflow);

        recordHistory(updatedWorkflow, approver, request.action(), workflow.getCurrentLevel(),
                request.comments(), prevStatus, updatedWorkflow.getStatus().name());

        log.info("Approval action {} processed for workflow id={} at level={}",
                request.action(), workflowId, workflow.getCurrentLevel());

        return workflowMapper.toResponse(workflowRepository.findById(workflowId).orElseThrow());
    }

    @Override
    public WorkflowResponse cancelWorkflow(Long workflowId, String username) {
        Workflow workflow = findWorkflowById(workflowId);
        User user = userService.findEntityByUsername(username);

        if (!workflow.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the workflow creator can cancel it");
        }

        if (workflow.getStatus() == WorkflowStatus.APPROVED || workflow.getStatus() == WorkflowStatus.CANCELLED) {
            throw new WorkflowException("Cannot cancel a workflow in " + workflow.getStatus() + " state");
        }

        String prevStatus = workflow.getStatus().name();
        workflow.setStatus(WorkflowStatus.CANCELLED);
        workflowRepository.save(workflow);

        recordHistory(workflow, user, ApprovalAction.REJECT, workflow.getCurrentLevel(),
                "Workflow cancelled by creator", prevStatus, WorkflowStatus.CANCELLED.name());

        return workflowMapper.toResponse(workflowRepository.findById(workflowId).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getPendingWorkflowsForApprover(String approverUsername) {
        User approver = userService.findEntityByUsername(approverUsername);
        return workflowRepository.findPendingWorkflowsForApprover(approver.getId())
                .stream()
                .map(workflowMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalHistoryResponse> getWorkflowHistory(Long workflowId) {
        findWorkflowById(workflowId); // validate exists
        return approvalHistoryRepository.findByWorkflowIdOrderByCreatedAtDesc(workflowId)
                .stream()
                .map(workflowMapper::toHistoryResponse)
                .toList();
    }

    // ---- Private helpers ----

    private void handleApprove(Workflow workflow, ApprovalStep step) {
        step.setStatus(WorkflowStatus.APPROVED);
        if (workflow.getCurrentLevel() >= workflow.getTotalLevels()) {
            workflow.setStatus(WorkflowStatus.APPROVED);
        } else {
            workflow.setCurrentLevel(workflow.getCurrentLevel() + 1);
        }
    }

    private void handleReject(Workflow workflow, ApprovalStep step) {
        step.setStatus(WorkflowStatus.REJECTED);
        workflow.setStatus(WorkflowStatus.REJECTED);
    }

    private void handleRequestChanges(Workflow workflow, ApprovalStep step) {
        step.setStatus(WorkflowStatus.CHANGES_REQUESTED);
        workflow.setStatus(WorkflowStatus.CHANGES_REQUESTED);
        workflow.setCurrentLevel(0);
    }

    private void recordHistory(Workflow workflow, User actor, ApprovalAction action,
                                int level, String comments, String fromStatus, String toStatus) {
        ApprovalHistory history = ApprovalHistory.builder()
                .workflow(workflow)
                .actor(actor)
                .action(action)
                .level(level)
                .comments(comments)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .build();
        approvalHistoryRepository.save(history);
    }

    private Workflow findWorkflowById(Long id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow", id));
    }

    private void validateStepLevels(List<CreateWorkflowRequest.ApprovalStepRequest> steps) {
        for (int i = 0; i < steps.size(); i++) {
            int expectedLevel = i + 1;
            if (steps.get(i).level() != expectedLevel) {
                throw new WorkflowException("Approval steps must have sequential levels starting from 1. " +
                        "Expected level " + expectedLevel + " but got " + steps.get(i).level());
            }
        }
    }
}
