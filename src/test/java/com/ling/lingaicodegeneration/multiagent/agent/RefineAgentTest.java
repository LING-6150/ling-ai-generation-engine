package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.RefineAgent;
import com.ling.lingaicodegeneration.ai.multiagent.model.RefineInput;
import com.ling.lingaicodegeneration.ai.multiagent.model.RefinementPlan;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RefineAgentTest {

    private final RefineAgent refineAgent = new RefineAgent();
    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "RefineAgent");

    private ReviewReport buildReport() {
        ReviewReport.Issue blocker = new ReviewReport.Issue(
                ReviewReport.Severity.BLOCKER, "index.html",
                "Missing navigation bar", "Add a <nav> element with links");
        ReviewReport.Issue major = new ReviewReport.Issue(
                ReviewReport.Severity.MAJOR, "style.css",
                "No mobile responsive styles", "Add @media queries for mobile");
        return ReviewReport.builder()
                .passed(false)
                .score(42)
                .issues(List.of(blocker, major))
                .summary("Critical structure and responsiveness issues found")
                .build();
    }

    @Test
    void buildRefinementRequest_containsReviewReportJson() {
        ReviewReport report = buildReport();
        RefinementPlan plan = new RefinementPlan(
                List.of("index.html", "style.css"), "Focus on structure", 1);
        RefineInput input = new RefineInput("/tmp/test_output", report, plan);

        String request = refineAgent.buildRefinementRequest(input);

        assertTrue(request.contains("BLOCKER"),   "must include BLOCKER severity");
        assertTrue(request.contains("MAJOR"),     "must include MAJOR severity");
        assertTrue(request.contains("index.html"),"must include issue file name");
        assertTrue(request.contains("Missing navigation bar"), "must include issue description");
        assertTrue(request.contains("Add a <nav>"), "must include suggestion");
        assertTrue(request.contains("42"),        "must include score");
        assertTrue(request.contains("style.css"), "must include second issue file");
    }

    @Test
    void buildRefinementRequest_includesAttemptNumber() {
        RefinementPlan plan = new RefinementPlan(List.of(), null, 2);
        RefineInput input = new RefineInput("/tmp/dir", buildReport(), plan);

        String request = refineAgent.buildRefinementRequest(input);

        assertTrue(request.contains("Attempt: 2 of 3"));
    }

    @Test
    void buildRefinementRequest_includesTargetFiles() {
        RefinementPlan plan = new RefinementPlan(
                List.of("src/App.vue", "src/router/index.js"), null, 1);
        RefineInput input = new RefineInput("/tmp/dir", buildReport(), plan);

        String request = refineAgent.buildRefinementRequest(input);

        assertTrue(request.contains("src/App.vue"));
        assertTrue(request.contains("src/router/index.js"));
    }

    @Test
    void buildRefinementRequest_noTargetFiles_showsAllFiles() {
        RefinementPlan plan = new RefinementPlan(List.of(), null, 1);
        RefineInput input = new RefineInput("/tmp/dir", buildReport(), plan);

        String request = refineAgent.buildRefinementRequest(input);

        assertTrue(request.contains("all files in the project"));
    }

    @Test
    void buildRefinementRequest_nullInstructions_usesDefault() {
        RefinementPlan plan = new RefinementPlan(List.of(), null, 1);
        RefineInput input = new RefineInput("/tmp/dir", buildReport(), plan);

        String request = refineAgent.buildRefinementRequest(input);

        assertTrue(request.contains("Follow the review report"));
    }
}
