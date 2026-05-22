package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.ReviewAgent;
import com.ling.lingaicodegeneration.ai.multiagent.llm.ReviewLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewInput;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport.Issue;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport.Severity;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewAgentTest {

    @Mock
    private ReviewLlmService reviewLlmService;

    @InjectMocks
    private ReviewAgent reviewAgent;

    private final AgentContext ctx = new AgentContext(1L, 1L, 3, "ReviewAgent");

    private ReviewInput buildInput() {
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("test project")
                .features(List.of("feature A"))
                .techConstraints(List.of())
                .complexity(Complexity.SIMPLE)
                .build();
        return new ReviewInput("/tmp/nonexistent-test-dir", spec);
    }

    private Issue issue(Severity severity, String file, String desc) {
        return Issue.builder()
                .severity(severity)
                .file(file)
                .description(desc)
                .suggestion("fix it")
                .build();
    }

    @Test
    void execute_returnsSortedIssues_blockerFirst() {
        ReviewReport unsorted = ReviewReport.builder()
                .passed(false)
                .score(35)
                .issues(List.of(
                        issue(Severity.MINOR,   "style.css",   "minor style issue"),
                        issue(Severity.BLOCKER, "index.html",  "page crashes"),
                        issue(Severity.MAJOR,   "app.js",      "feature missing"),
                        issue(Severity.MAJOR,   "nav.html",    "broken navigation")
                ))
                .summary("Multiple issues")
                .build();

        when(reviewLlmService.review(anyString())).thenReturn(unsorted);

        ReviewReport result = reviewAgent.execute(buildInput(), ctx);

        assertNotNull(result);
        assertEquals(4, result.getIssues().size());
        assertEquals(Severity.BLOCKER, result.getIssues().get(0).getSeverity());
        assertEquals(Severity.MAJOR,   result.getIssues().get(1).getSeverity());
        assertEquals(Severity.MAJOR,   result.getIssues().get(2).getSeverity());
        assertEquals(Severity.MINOR,   result.getIssues().get(3).getSeverity());
    }

    @Test
    void execute_passesThrough_score_and_summary() {
        ReviewReport report = ReviewReport.builder()
                .passed(true)
                .score(92)
                .issues(List.of())
                .summary("Code looks good")
                .build();

        when(reviewLlmService.review(anyString())).thenReturn(report);

        ReviewReport result = reviewAgent.execute(buildInput(), ctx);

        assertTrue(result.getPassed());
        assertEquals(92, result.getScore());
        assertEquals("Code looks good", result.getSummary());
    }

    @Test
    void execute_onException_returnsFallbackReport() {
        when(reviewLlmService.review(anyString()))
                .thenThrow(new RuntimeException("LLM parse error"));

        ReviewReport result = reviewAgent.execute(buildInput(), ctx);

        assertNotNull(result);
        assertFalse(result.getPassed());
        assertEquals(0, result.getScore());
        assertEquals("Automated review unavailable, manual check recommended", result.getSummary());
        assertEquals(1, result.getIssues().size());
        assertEquals(Severity.MAJOR, result.getIssues().get(0).getSeverity());
        assertTrue(result.getIssues().get(0).getDescription().contains("LLM parse error"));
        assertEquals("Skip review and proceed to user", result.getIssues().get(0).getSuggestion());
    }

    @Test
    void execute_singleIssue_returnedWithoutSorting() {
        ReviewReport report = ReviewReport.builder()
                .passed(false)
                .score(50)
                .issues(List.of(issue(Severity.MAJOR, "index.html", "only issue")))
                .summary("One issue")
                .build();

        when(reviewLlmService.review(anyString())).thenReturn(report);

        ReviewReport result = reviewAgent.execute(buildInput(), ctx);

        assertEquals(1, result.getIssues().size());
        assertEquals(Severity.MAJOR, result.getIssues().get(0).getSeverity());
    }

    @Test
    void execute_delegatesToReviewLlmService() {
        ReviewReport report = ReviewReport.builder()
                .passed(true).score(85).issues(List.of()).summary("ok").build();
        when(reviewLlmService.review(anyString())).thenReturn(report);

        reviewAgent.execute(buildInput(), ctx);

        verify(reviewLlmService, times(1)).review(anyString());
    }
}
