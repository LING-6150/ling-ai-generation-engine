package com.ling.lingaicodegeneration.multiagent.util;

import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import com.ling.lingaicodegeneration.ai.multiagent.util.ContextPruningService;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContextPruningServiceTest {

    private final ContextPruningService service = new ContextPruningService();

    @Test
    void pruneForCodeGen_removesReviewOnlyPlanFieldsAndLimitsImages() {
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("A restaurant website")
                .features(List.of("menu", "reservation", "gallery", "reviews", "map", "newsletter"))
                .techConstraints(List.of("vanilla js"))
                .complexity(Complexity.MEDIUM)
                .build();
        String images = String.join("\n",
                "https://img/1", "https://img/2", "https://img/3", "https://img/4",
                "https://img/5", "https://img/6", "https://img/7");
        TaskGraph plan = new TaskGraph(true, 2, "Use semantic HTML", "Review responsiveness");

        CodeGenInput pruned = service.pruneForCodeGen(new CodeGenInput(spec, images, plan));

        assertEquals(5, pruned.requirementSpec().getFeatures().size());
        assertEquals(6, pruned.imageListStr().split("\\R").length);
        assertEquals("Use semantic HTML", pruned.taskGraph().codeGenHints());
        assertNull(pruned.taskGraph().reviewFocusAreas());
    }

    @Test
    void pruneForRefine_keepsActionableIssuesAndTargets() {
        ReviewReport report = ReviewReport.builder()
                .passed(false)
                .score(60)
                .summary("Needs work")
                .issues(List.of(
                        issue(ReviewReport.Severity.MINOR, "a.html"),
                        issue(ReviewReport.Severity.BLOCKER, "b.html"),
                        issue(ReviewReport.Severity.MAJOR, "c.html")
                ))
                .build();
        RefinementPlan plan = new RefinementPlan(
                List.of("a.html", "b.html", "c.html", "d.html", "e.html", "f.html", "g.html"),
                "Fix important issues",
                1
        );

        RefineInput pruned = service.pruneForRefine(new RefineInput("/tmp/code", report, plan));

        assertEquals(2, pruned.reviewReport().getIssues().size());
        assertEquals(ReviewReport.Severity.BLOCKER,
                pruned.reviewReport().getIssues().getFirst().getSeverity());
        assertEquals(6, pruned.refinementPlan().targetFiles().size());
    }

    private ReviewReport.Issue issue(ReviewReport.Severity severity, String file) {
        return ReviewReport.Issue.builder()
                .severity(severity)
                .file(file)
                .description("description")
                .suggestion("suggestion")
                .build();
    }
}
