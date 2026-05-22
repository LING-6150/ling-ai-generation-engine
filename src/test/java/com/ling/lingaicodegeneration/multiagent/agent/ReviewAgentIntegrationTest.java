package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.ReviewAgent;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewInput;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class ReviewAgentIntegrationTest {

    @Resource
    private ReviewAgent reviewAgent;

    @TempDir
    Path tempDir;

    @Test
    void execute_withFlawedHtml_detectsIssues() throws IOException {
        // HTML with an unclosed div and missing alt attributes — clear structural issues
        String flawedHtml = """
                <!DOCTYPE html>
                <html>
                <head><title>Test Page</title></head>
                <body>
                  <div class="container">
                    <img src="logo.png">
                    <div class="inner">
                      <p>Hello world
                    <!-- unclosed div.inner and unclosed p -->
                </body>
                </html>
                """;
        Files.writeString(tempDir.resolve("index.html"), flawedHtml);

        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("A simple landing page with logo and greeting")
                .features(List.of("display a logo image", "show a greeting paragraph"))
                .techConstraints(List.of("semantic HTML5", "no frameworks"))
                .complexity(Complexity.SIMPLE)
                .build();

        ReviewInput input = new ReviewInput(tempDir.toString(), spec);
        AgentContext ctx = new AgentContext(999L, 999L, 1, "ReviewAgent");

        ReviewReport report = reviewAgent.execute(input, ctx);

        assertNotNull(report, "Report must not be null");
        assertFalse(report.getPassed(), "Flawed HTML must not pass review");
        assertNotNull(report.getIssues(), "Issues list must not be null");
        assertFalse(report.getIssues().isEmpty(), "At least one issue must be reported");

        // BLOCKER or MAJOR should exist due to unclosed tags / missing alt
        boolean hasSignificantIssue = report.getIssues().stream()
                .anyMatch(i -> i.getSeverity() == ReviewReport.Severity.BLOCKER
                        || i.getSeverity() == ReviewReport.Severity.MAJOR);
        assertTrue(hasSignificantIssue, "Unclosed tags or missing alt must produce BLOCKER or MAJOR issue");

        // Issues must be sorted (BLOCKER before MAJOR before MINOR)
        List<ReviewReport.Issue> issues = report.getIssues();
        for (int i = 0; i < issues.size() - 1; i++) {
            ReviewReport.Severity cur  = issues.get(i).getSeverity();
            ReviewReport.Severity next = issues.get(i + 1).getSeverity();
            if (cur != null && next != null) {
                assertTrue(cur.ordinal() <= next.ordinal(),
                        "Issues must be sorted by severity: " + cur + " before " + next);
            }
        }
    }
}
