package com.ling.lingaicodegeneration;

import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.ai.langgraph4j.workflow.CodeGenWorkflow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.annotation.Resource;

@SpringBootTest
public class CodeGenWorkflowTest {

    @Resource
    private CodeGenWorkflow codeGenWorkflow;

    @Test
    void testSimpleHtmlWorkflow() {
        WorkflowContext result = codeGenWorkflow.executeWorkflow(
                "Create a simple personal bio page", 1L);
        System.out.println("Generation type: " + result.getGenerationType());
        System.out.println("Generated code dir: " + result.getGeneratedCodeDir());
        System.out.println("Build result dir: " + result.getBuildResultDir());
        System.out.println("Quality result: " + result.getQualityResult());
    }

    @Test
    void testEcommerceWorkflow() {
        WorkflowContext result = codeGenWorkflow.executeWorkflow(
                "Create an e-commerce website with product listing and shopping cart", 2L);
        System.out.println("Generation type: " + result.getGenerationType());
        System.out.println("Generated code dir: " + result.getGeneratedCodeDir());
        System.out.println("Build result dir: " + result.getBuildResultDir());
        System.out.println("Quality result: " + result.getQualityResult());
    }
}
