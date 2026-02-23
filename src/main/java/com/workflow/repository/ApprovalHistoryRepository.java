package com.workflow.repository;

import com.workflow.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findByWorkflowIdOrderByCreatedAtDesc(Long workflowId);

    List<ApprovalHistory> findByActorIdOrderByCreatedAtDesc(Long actorId);
}
