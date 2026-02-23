package com.workflow.service;

import com.workflow.dto.request.ApprovalActionRequest;
import com.workflow.dto.request.CreateWorkflowRequest;
import com.workflow.dto.response.WorkflowResponse;
import com.workflow.entity.ApprovalStep;
import com.workflow.entity.User;
import com.workflow.entity.Workflow;
import com.workflow.enums.ApprovalAction;
import com.workflow.enums.Role;
import com.workflow.enums.WorkflowStatus;
import com.workflow.exception.UnauthorizedException;
import com.workflow.exception.WorkflowException;
import com.workflow.mapper.WorkflowMapper;
import com.workflow.repository.ApprovalHistoryRepository;
import com.workflow.repository.ApprovalStepRepository;
import com.workflow.repository.WorkflowRepository;
import com.workflow.service.impl.WorkflowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkflowService Tests")
class WorkflowServiceTest {

    @Mock private WorkflowRepository workflowRepository;
    @Mock private ApprovalStepRepository approvalStepRepository;
    @Mock private ApprovalHistoryRepository approvalHistoryRepository;
    @Mock private UserService userService;
    @Mock private WorkflowMapper workflowMapper;

    @InjectMocks private WorkflowServiceImpl workflowService;

    private User creator;
    private User approver;
    private Workflow workflow;

    @BeforeEach
    void setUp() {
        creator = User.builder().id(1L).username("creator").role(Role.EMPLOYEE).build();
        approver = User.builder().id(2L).username("approver").role(Role.MANAGER).build();

        workflow = Workflow.builder()
                .id(1L)
                .title("Test Workflow")
                .status(WorkflowStatus.DRAFT)
                .createdBy(creator)
                .currentLevel(0)
                .totalLevels(1)
                .build();
    }

    @Test
    @DisplayName("Should submit workflow successfully")
    void shouldSubmitWorkflowSuccessfully() {
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));
        when(userService.findEntityByUsername("creator")).thenReturn(creator);
        when(workflowRepository.save(any())).thenReturn(workflow);
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));
        when(workflowMapper.toResponse(any())).thenReturn(mock(WorkflowResponse.class));

        workflowService.submitWorkflow(1L, "creator");

        verify(workflowRepository, atLeastOnce()).save(any(Workflow.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when non-creator tries to submit")
    void shouldThrowWhenNonCreatorSubmits() {
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));
        when(userService.findEntityByUsername("other")).thenReturn(
                User.builder().id(99L).username("other").build()
        );

        assertThatThrownBy(() -> workflowService.submitWorkflow(1L, "other"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw WorkflowException when submitting non-DRAFT workflow")
    void shouldThrowWhenSubmittingNonDraftWorkflow() {
        workflow.setStatus(WorkflowStatus.IN_PROGRESS);
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));
        when(userService.findEntityByUsername("creator")).thenReturn(creator);

        assertThatThrownBy(() -> workflowService.submitWorkflow(1L, "creator"))
                .isInstanceOf(WorkflowException.class);
    }

    @Test
    @DisplayName("Should validate sequential approval step levels")
    void shouldValidateSequentialLevels() {
        when(userService.findEntityByUsername("creator")).thenReturn(creator);

        CreateWorkflowRequest request = new CreateWorkflowRequest(
                "Test", "desc", null,
                List.of(
                        new CreateWorkflowRequest.ApprovalStepRequest(2L, 1, "Step 1"),
                        new CreateWorkflowRequest.ApprovalStepRequest(3L, 3, "Step 3") // Level 2 skipped
                )
        );

        when(userService.findEntityById(2L)).thenReturn(approver);
        when(workflowRepository.save(any())).thenReturn(workflow);
        when(workflowRepository.findById(any())).thenReturn(Optional.of(workflow));
        when(workflowMapper.toResponse(any())).thenReturn(mock(WorkflowResponse.class));

        assertThatThrownBy(() -> workflowService.createWorkflow(request, "creator"))
                .isInstanceOf(WorkflowException.class)
                .hasMessageContaining("sequential levels");
    }
}
