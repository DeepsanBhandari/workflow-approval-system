package com.workflow.repository;

import com.workflow.entity.Workflow;
import com.workflow.enums.WorkflowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    Page<Workflow> findByCreatedById(Long userId, Pageable pageable);

    Page<Workflow> findByStatus(WorkflowStatus status, Pageable pageable);

    @Query("SELECT w FROM Workflow w JOIN w.approvalSteps s WHERE s.approver.id = :approverId")
    Page<Workflow> findByApproverId(@Param("approverId") Long approverId, Pageable pageable);

    @Query("SELECT w FROM Workflow w JOIN w.approvalSteps s WHERE s.approver.id = :approverId AND s.level = w.currentLevel AND w.status = 'IN_PROGRESS'")
    List<Workflow> findPendingWorkflowsForApprover(@Param("approverId") Long approverId);

    List<Workflow> findByStatusIn(List<WorkflowStatus> statuses);
}
