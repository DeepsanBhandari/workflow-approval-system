package com.workflow.mapper;

import com.workflow.dto.response.ApprovalHistoryResponse;
import com.workflow.dto.response.ApprovalStepResponse;
import com.workflow.dto.response.WorkflowResponse;
import com.workflow.entity.ApprovalHistory;
import com.workflow.entity.ApprovalStep;
import com.workflow.entity.Workflow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface WorkflowMapper {

    @Mapping(target = "status", expression = "java(workflow.getStatus().name())")
    WorkflowResponse toResponse(Workflow workflow);

    @Mapping(target = "status", expression = "java(step.getStatus().name())")
    ApprovalStepResponse toStepResponse(ApprovalStep step);

    @Mapping(target = "action", expression = "java(history.getAction().name())")
    ApprovalHistoryResponse toHistoryResponse(ApprovalHistory history);
}
