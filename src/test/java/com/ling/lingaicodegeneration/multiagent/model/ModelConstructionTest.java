package com.ling.lingaicodegeneration.multiagent.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import dev.langchain4j.internal.Json;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelConstructionTest {

    // ── AgentContext ────────────────────────────────────────────────────────

    @Test
    void agentContext_withAgentName_returnsNewInstance() {
        AgentContext original = new AgentContext(1L, 2L, 3, "original");
        AgentContext renamed = original.withAgentName("renamed");

        assertEquals("renamed", renamed.agentName());
        assertEquals("original", original.agentName()); // immutable
        assertEquals(original.appId(), renamed.appId());
        assertEquals(original.retryBudget(), renamed.retryBudget());
    }

    @Test
    void agentContext_withRetryBudget_returnsNewInstance() {
        AgentContext original = new AgentContext(1L, 2L, 3, "test");
        AgentContext updated = original.withRetryBudget(1);

        assertEquals(1, updated.retryBudget());
        assertEquals(3, original.retryBudget()); // immutable
    }

    // ── RequirementSpec (Lombok) ─────────────────────────────────────────────

    @Test
    void requirementSpec_jacksonRoundtrip() throws Exception {
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("A landing page")
                .features(List.of("hero section", "contact form"))
                .techConstraints(List.of("no framework"))
                .complexity(Complexity.SIMPLE)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(spec);
        RequirementSpec deserialized = mapper.readValue(json, RequirementSpec.class);

        assertEquals(spec, deserialized);
        assertEquals(CodeGenTypeEnum.HTML, deserialized.getCodeGenType());
        assertEquals(2, deserialized.getFeatures().size());
    }

    @Test
    void requirementSpec_langchain4jJsonRoundtrip() {
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.VUE_PROJECT)
                .projectDescription("An e-commerce site")
                .features(List.of("product listing", "cart"))
                .techConstraints(List.of())
                .complexity(Complexity.COMPLEX)
                .build();

        String json = Json.toJson(spec);
        RequirementSpec deserialized = Json.fromJson(json, RequirementSpec.class);

        assertNotNull(deserialized);
        assertEquals(CodeGenTypeEnum.VUE_PROJECT, deserialized.getCodeGenType());
        assertEquals(Complexity.COMPLEX, deserialized.getComplexity());
    }

    // ── TaskGraph (record) — Q3 validation ──────────────────────────────────

    @Test
    void taskGraph_jacksonRoundtrip() throws Exception {
        TaskGraph graph = new TaskGraph(true, 2, "use Tailwind CSS", "check responsive layout");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(graph);
        TaskGraph deserialized = mapper.readValue(json, TaskGraph.class);

        assertEquals(graph, deserialized);
        assertTrue(deserialized.parallelizeAsset());
        assertEquals(2, deserialized.suggestedRetries());
    }

    @Test
    void taskGraph_langchain4jJsonRoundtrip() {
        TaskGraph graph = new TaskGraph(false, 1, null, "focus on accessibility");

        String json = Json.toJson(graph);
        TaskGraph deserialized = Json.fromJson(json, TaskGraph.class);

        assertNotNull(deserialized);
        assertFalse(deserialized.parallelizeAsset());
        assertEquals("focus on accessibility", deserialized.reviewFocusAreas());
    }

    // ── ReviewReport (Lombok, nested classes) ────────────────────────────────

    @Test
    void reviewReport_jacksonRoundtrip() throws Exception {
        ReviewReport.Issue issue = ReviewReport.Issue.builder()
                .severity(ReviewReport.Severity.BLOCKER)
                .file("src/App.vue")
                .description("Missing error handling")
                .suggestion("Add try-catch around API calls")
                .build();

        ReviewReport report = ReviewReport.builder()
                .passed(false)
                .score(42)
                .issues(List.of(issue))
                .summary("Critical issue found")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(report);
        ReviewReport deserialized = mapper.readValue(json, ReviewReport.class);

        assertFalse(deserialized.getPassed());
        assertEquals(42, deserialized.getScore());
        assertEquals(1, deserialized.getIssues().size());
        assertEquals(ReviewReport.Severity.BLOCKER, deserialized.getIssues().get(0).getSeverity());
        assertFalse(deserialized.isDegraded(), "Normal ReviewReport must have degraded=false after roundtrip");
    }

    @Test
    void reviewReport_langchain4jJsonRoundtrip() {
        ReviewReport report = ReviewReport.builder()
                .passed(true)
                .score(88)
                .issues(List.of())
                .summary("Looks good")
                .build();

        String json = Json.toJson(report);
        ReviewReport deserialized = Json.fromJson(json, ReviewReport.class);

        assertNotNull(deserialized);
        assertTrue(deserialized.getPassed());
        assertEquals(88, deserialized.getScore());
        assertFalse(deserialized.isDegraded(), "Normal ReviewReport must have degraded=false after LangChain4j roundtrip");
    }

    @Test
    void reviewReport_degradedFlag_isNotSetByBuilder_default() {
        // Verify that omitting .degraded() in builder results in false (not null or true)
        ReviewReport report = ReviewReport.builder()
                .passed(false).score(0).issues(List.of()).summary("degraded test").build();
        assertFalse(report.isDegraded());

        // Verify explicit degraded=true survives roundtrip
        ReviewReport fallback = ReviewReport.builder()
                .passed(false).score(0).issues(List.of()).summary("fallback").degraded(true).build();
        assertTrue(fallback.isDegraded());
        String json = Json.toJson(fallback);
        ReviewReport deserialized = Json.fromJson(json, ReviewReport.class);
        assertTrue(deserialized.isDegraded(), "degraded=true must survive LangChain4j roundtrip");
    }

    // ── RefinementPlan (record) ──────────────────────────────────────────────

    @Test
    void refinementPlan_jacksonRoundtrip() throws Exception {
        RefinementPlan plan = new RefinementPlan(
                List.of("src/App.vue", "src/api.js"),
                "Fix null pointer in API call",
                1
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(plan);
        RefinementPlan deserialized = mapper.readValue(json, RefinementPlan.class);

        assertEquals(plan, deserialized);
        assertEquals(2, deserialized.targetFiles().size());
    }

    // ── CodeGenInput (record with buildPrompt) ───────────────────────────────

    @Test
    void codeGenInput_buildPrompt_withImages() {
        RequirementSpec spec = RequirementSpec.builder()
                .projectDescription("A photo gallery site")
                .features(List.of())
                .techConstraints(List.of())
                .codeGenType(CodeGenTypeEnum.HTML)
                .complexity(Complexity.SIMPLE)
                .build();
        TaskGraph graph = new TaskGraph(true, 2, "use CSS grid", null);
        String images = "https://example.com/img1.jpg\nhttps://example.com/img2.jpg";

        CodeGenInput input = new CodeGenInput(spec, images, graph);
        String prompt = input.buildPrompt();

        assertTrue(prompt.contains("A photo gallery site"));
        assertTrue(prompt.contains("Available Image Resources"));
        assertTrue(prompt.contains("https://example.com/img1.jpg"));
        assertTrue(prompt.contains("use CSS grid"));
    }

    @Test
    void codeGenInput_buildPrompt_withoutImages() {
        RequirementSpec spec = RequirementSpec.builder()
                .projectDescription("A todo app")
                .features(List.of())
                .techConstraints(List.of())
                .codeGenType(CodeGenTypeEnum.HTML)
                .complexity(Complexity.SIMPLE)
                .build();
        TaskGraph graph = new TaskGraph(false, 1, null, null);

        CodeGenInput input = new CodeGenInput(spec, "", graph);
        String prompt = input.buildPrompt();

        assertTrue(prompt.contains("A todo app"));
        assertFalse(prompt.contains("Available Image Resources"));
    }

    // ── RefineInput (record) ─────────────────────────────────────────────────

    @Test
    void refineInput_construction() {
        ReviewReport report = ReviewReport.builder()
                .passed(false).score(50).issues(List.of()).summary("needs work").build();
        RefinementPlan plan = new RefinementPlan(List.of("index.html"), "fix layout", 1);

        RefineInput input = new RefineInput("/tmp/code/html_123", report, plan);

        assertEquals("/tmp/code/html_123", input.generatedCodeDir());
        assertEquals(1, input.refinementPlan().attemptNumber());
    }
}
