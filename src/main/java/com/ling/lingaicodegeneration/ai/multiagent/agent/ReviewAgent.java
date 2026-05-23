package com.ling.lingaicodegeneration.ai.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.llm.ReviewLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewInput;
import com.ling.lingaicodegeneration.ai.multiagent.model.ReviewReport;
import com.ling.lingaicodegeneration.ai.multiagent.util.CodeContextBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class ReviewAgent implements Agent<ReviewInput, ReviewReport> {

    @Resource
    private ReviewLlmService reviewLlmService;

    @Override
    public ReviewReport execute(ReviewInput input, AgentContext ctx) {
        log.info("ReviewAgent executing, appId: {}, dir: {}", ctx.appId(), input.generatedCodeDir());
        try {
            String context = CodeContextBuilder.buildReviewContext(
                    input.generatedCodeDir(), input.requirementSpec());

            ReviewReport report = reviewLlmService.review(context);
            // Defensive: reset degraded regardless of what LLM returned — this field
            // is a workflow-control flag, not a value the LLM should ever set.
            report.setDegraded(false);
            report = sortIssues(report);

            log.info("ReviewAgent completed, appId: {}, passed: {}, score: {}, issues: {}",
                    ctx.appId(), report.getPassed(), report.getScore(),
                    report.getIssues() == null ? 0 : report.getIssues().size());
            return report;

        } catch (Exception e) {
            log.warn("ReviewAgent output parsing failed, returning fallback report, appId: {}: {}",
                    ctx.appId(), e.getMessage());
            return fallbackReport(e);
        }
    }

    private ReviewReport sortIssues(ReviewReport report) {
        if (report.getIssues() == null || report.getIssues().size() <= 1) return report;
        List<ReviewReport.Issue> sorted = report.getIssues().stream()
                .sorted(Comparator.comparingInt(i ->
                        i.getSeverity() == null ? Integer.MAX_VALUE : i.getSeverity().ordinal()))
                .toList();
        report.setIssues(sorted);
        return report;
    }

    private ReviewReport fallbackReport(Exception e) {
        return ReviewReport.builder()
                .passed(false)
                .score(0)
                .degraded(true)
                .issues(List.of(ReviewReport.Issue.builder()
                        .severity(ReviewReport.Severity.MAJOR)
                        .description("Review LLM output parsing failed: " + e.getMessage())
                        .suggestion("Skip review and proceed to user")
                        .build()))
                .summary("Automated review unavailable, manual check recommended")
                .build();
    }
}
