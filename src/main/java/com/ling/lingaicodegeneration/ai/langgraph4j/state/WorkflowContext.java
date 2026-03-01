package com.ling.lingaicodegeneration.ai.langgraph4j.state;

import com.ling.lingaicodegeneration.ai.langgraph4j.model.QualityResult;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.Map;

/**
 * Workflow state context - shared across all workflow nodes
 * Stored as a value in MessageState under WORKFLOW_CONTEXT_KEY
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowContext implements Serializable {

    /**
     * Key used to store WorkflowContext in MessageState
     */
    public static final String WORKFLOW_CONTEXT_KEY = "workflowContext";

    /** Current execution step */
    private String currentStep;

    /** App ID */
    private Long appId;

    /** Original user prompt */
    private String originalPrompt;

    /** Collected image list as string */
    private String imageListStr;

    /** Enhanced prompt with image info */
    private String enhancedPrompt;

    /** Code generation type */
    private CodeGenTypeEnum generationType;

    /** Generated code directory */
    private String generatedCodeDir;

    /** Build result directory */
    private String buildResultDir;

    /** Code quality check result */
    private QualityResult qualityResult;

    /** Error message */
    private String errorMessage;

    // ========== Helper methods ==========

    /**
     * Get WorkflowContext from MessageState
     */
    public static WorkflowContext getContext(
            org.bsc.langgraph4j.state.AgentState state) {
        return (WorkflowContext) state.data()
                .get(WORKFLOW_CONTEXT_KEY);
    }

    /**
     * Save WorkflowContext to MessageState
     */
    public static Map<String, Object> saveContext(WorkflowContext context) {
        return Map.of(WORKFLOW_CONTEXT_KEY, context);
    }
}