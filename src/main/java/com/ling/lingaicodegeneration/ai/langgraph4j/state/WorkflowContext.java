package com.ling.lingaicodegeneration.ai.langgraph4j.state;

import com.ling.lingaicodegeneration.ai.langgraph4j.model.QualityResult;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bsc.langgraph4j.state.AgentState;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowContext implements Serializable {

    public static final String WORKFLOW_CONTEXT_KEY = "workflowContext";

    private String currentStep;
    private Long appId;
    private String originalPrompt;
    private String imageListStr;
    private String enhancedPrompt;
    private CodeGenTypeEnum generationType;
    private String generatedCodeDir;
    private String buildResultDir;
    private QualityResult qualityResult;
    private String errorMessage;

    @Builder.Default
    private int retryCount = 0;

    public static WorkflowContext getContext(AgentState state) {
        return (WorkflowContext) state.data().get(WORKFLOW_CONTEXT_KEY);
    }

    public static Map<String, Object> saveContext(WorkflowContext context) {
        return Map.of(WORKFLOW_CONTEXT_KEY, context);
    }
}