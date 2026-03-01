package com.ling.lingaicodegeneration.ai.langgraph4j.workflow;

import com.ling.lingaicodegeneration.ai.langgraph4j.node.*;
import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.ai.langgraph4j.model.QualityResult;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.AppenderChannel;
import org.bsc.langgraph4j.state.Channel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;

@Slf4j
@Component
public class CodeGenWorkflow {

    /**
     * Create compiled workflow
     */
    private CompiledGraph<AgentState> createWorkflow() {
        try {
            return new StateGraph<>(AgentState::new)
                    // Add nodes
                    .addNode("image_collector", ImageCollectorNode.create())
                    .addNode("prompt_enhancer", PromptEnhancerNode.create())
                    .addNode("router", RouterNode.create())
                    .addNode("code_generator", CodeGeneratorNode.create())
                    .addNode("code_quality_check", CodeQualityCheckNode.create())
                    .addNode("project_builder", ProjectBuilderNode.create())
                    // Add edges
                    .addEdge(START, "image_collector")
                    .addEdge("image_collector", "prompt_enhancer")
                    .addEdge("prompt_enhancer", "router")
                    .addEdge("router", "code_generator")
                    .addEdge("code_generator", "code_quality_check")
                    // Conditional edge after quality check
                    .addConditionalEdges("code_quality_check",
                            edge_async(this::routeAfterQualityCheck),
                            Map.of(
                                    "build", "project_builder",
                                    "skip_build", END,
                                    "fail", "code_generator"
                            ))
                    .addEdge("project_builder", END)
                    .compile();
        } catch (GraphStateException e) {
            throw new RuntimeException("Failed to create workflow", e);
        }
    }

    /**
     * Execute workflow (blocking)
     */
    public WorkflowContext executeWorkflow(String originalPrompt, Long appId) {
        CompiledGraph<AgentState> workflow = createWorkflow();
        WorkflowContext initialContext = WorkflowContext.builder()
                .originalPrompt(originalPrompt)
                .appId(appId)
                .currentStep("Initializing")
                .build();
        log.info("Starting code generation workflow, prompt: {}",
                originalPrompt.substring(0, Math.min(originalPrompt.length(), 50)));
        WorkflowContext finalContext = null;
        int stepCounter = 1;
        for (NodeOutput<AgentState> step : workflow.stream(
                Map.of(WorkflowContext.WORKFLOW_CONTEXT_KEY, initialContext))) {
            log.info("---- Step {} completed ----", stepCounter);
            WorkflowContext currentContext = WorkflowContext.getContext(step.state());
            if (currentContext != null) {
                finalContext = currentContext;
                log.info("Current step: {}", currentContext.getCurrentStep());
            }
            stepCounter++;
        }
        log.info("Workflow completed!");
        return finalContext;
    }

    /**
     * Execute workflow (Flux streaming output)
     */
    public Flux<String> executeWorkflowWithFlux(String originalPrompt, Long appId) {
        return Flux.create(sink -> {
            Thread.startVirtualThread(() -> {
                try {
                    CompiledGraph<AgentState> workflow = createWorkflow();
                    WorkflowContext initialContext = WorkflowContext.builder()
                            .originalPrompt(originalPrompt)
                            .appId(appId)
                            .currentStep("Initializing")
                            .build();
                    sink.next(formatSseEvent("workflow_start", Map.of(
                            "message", "Starting code generation workflow",
                            "originalPrompt", originalPrompt
                    )));
                    int stepCounter = 1;
                    for (NodeOutput<AgentState> step : workflow.stream(
                            Map.of(WorkflowContext.WORKFLOW_CONTEXT_KEY, initialContext))) {
                        log.info("---- Step {} completed ----", stepCounter);
                        WorkflowContext currentContext =
                                WorkflowContext.getContext(step.state());
                        if (currentContext != null) {
                            sink.next(formatSseEvent("step_completed", Map.of(
                                    "stepNumber", stepCounter,
                                    "currentStep", currentContext.getCurrentStep()
                            )));
                            log.info("Current step: {}", currentContext.getCurrentStep());
                        }
                        stepCounter++;
                    }
                    sink.next(formatSseEvent("workflow_completed", Map.of(
                            "message", "Code generation workflow completed!"
                    )));
                    log.info("Workflow completed!");
                    sink.complete();
                } catch (Exception e) {
                    log.error("Workflow execution error: {}", e.getMessage(), e);
                    sink.next(formatSseEvent("workflow_error", Map.of(
                            "error", e.getMessage(),
                            "message", "Workflow execution failed"
                    )));
                    sink.error(e);
                }
            });
        });
    }

    /**
     * Route after quality check
     */
    private String routeAfterQualityCheck(AgentState state) {
        WorkflowContext context = WorkflowContext.getContext(state);
        QualityResult qualityResult = context.getQualityResult();
        if (qualityResult == null || !qualityResult.getIsValid()) {
            log.error("Code quality check failed, regenerating code");
            return "fail";
        }
        log.info("Code quality check passed, proceeding to build");
        CodeGenTypeEnum generationType = context.getGenerationType();
        if (generationType == CodeGenTypeEnum.VUE_PROJECT) {
            return "build";
        } else {
            return "skip_build";
        }
    }

    /**
     * Format SSE event helper
     */
    private String formatSseEvent(String eventType, Object data) {
        try {
            String jsonData = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(data);
            return "event: " + eventType + "\ndata: " + jsonData + "\n\n";
        } catch (Exception e) {
            log.error("Failed to format SSE event: {}", e.getMessage(), e);
            return "event: error\ndata: {\"error\":\"format error\"}\n\n";
        }
    }
}