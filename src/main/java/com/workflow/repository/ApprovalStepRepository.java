package com.workflow.repository;

import com.workflow.entity.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {

    List<ApprovalStep> findByWorkflowId(Long workflowId);

    Optional<ApprovalStep> findByWorkflowIdAndLevel(Long workflowId, int level);

    List<ApprovalStep> findByApproverId(Long approverId);
}
