package com.ling.lingaicodegeneration.ai.multiagent.util;

import com.ling.lingaicodegeneration.ai.multiagent.model.*;
import dev.langchain4j.internal.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ContextPruningService {

    private static final int MAX_IMAGE_LINES = 6;
    private static final int MAX_IMAGE_CHARS = 2_000;
    private static final int MAX_FEATURES = 5;
    private static final int MAX_CONSTRAINTS = 5;
    private static final int MAX_TEXT_CHARS = 1_200;
    private static final int MAX_REFINEMENT_ISSUES = 5;
    private static final int MAX_TARGET_FILES = 6;

    @Value("${context.pruning.diagnostics.enabled:false}")
    private boolean contextPruningDiagnosticsEnabled;

    public CodeGenInput pruneForCodeGen(CodeGenInput input) {
        CodeGenInput pruned = new CodeGenInput(
                pruneRequirementSpec(input.requirementSpec()),
                pruneImageList(input.imageListStr()),
                pruneTaskGraphForCodeGen(input.taskGraph())
        );
        logReduction("CodeGenInput", input, pruned);
        logCodeGenDiagnostics(input, pruned);
        return pruned;
    }

    public ReviewInput pruneForReview(ReviewInput input) {
        ReviewInput pruned = new ReviewInput(
                input.generatedCodeDir(),
                pruneRequirementSpec(input.requirementSpec())
        );
        logReduction("ReviewInput", input, pruned);
        return pruned;
    }

    public RefineInput pruneForRefine(RefineInput input) {
        RefineInput pruned = new RefineInput(
                input.generatedCodeDir(),
                pruneReviewReport(input.reviewReport()),
                pruneRefinementPlan(input.refinementPlan())
        );
        logReduction("RefineInput", input, pruned);
        return pruned;
    }

    public RequirementSpec pruneRequirementSpec(RequirementSpec spec) {
        if (spec == null) return null;
        return RequirementSpec.builder()
                .codeGenType(spec.getCodeGenType())
                .projectDescription(truncate(spec.getProjectDescription(), MAX_TEXT_CHARS))
                .features(limit(spec.getFeatures(), MAX_FEATURES))
                .techConstraints(limit(spec.getTechConstraints(), MAX_CONSTRAINTS))
                .complexity(spec.getComplexity())
                .build();
    }

    private TaskGraph pruneTaskGraphForCodeGen(TaskGraph taskGraph) {
        if (taskGraph == null) return null;
        return new TaskGraph(
                taskGraph.parallelizeAsset(),
                taskGraph.suggestedRetries(),
                truncate(taskGraph.codeGenHints(), MAX_TEXT_CHARS),
                null
        );
    }

    private String pruneImageList(String imageListStr) {
        if (imageListStr == null || imageListStr.isBlank()) return imageListStr;
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String line : imageListStr.split("\\R")) {
            if (line.isBlank()) continue;
            if (count >= MAX_IMAGE_LINES) break;
            if (sb.length() + line.length() + 1 > MAX_IMAGE_CHARS) break;
            if (!sb.isEmpty()) sb.append('\n');
            sb.append(line);
            count++;
        }
        return sb.toString();
    }

    private ReviewReport pruneReviewReport(ReviewReport report) {
        if (report == null) return null;
        List<ReviewReport.Issue> issues = report.getIssues();
        List<ReviewReport.Issue> prunedIssues = new ArrayList<>();
        if (issues != null) {
            for (ReviewReport.Issue issue : issues) {
                if (issue == null) continue;
                ReviewReport.Severity severity = issue.getSeverity();
                if (severity != ReviewReport.Severity.BLOCKER
                        && severity != ReviewReport.Severity.MAJOR) {
                    continue;
                }
                prunedIssues.add(copyIssue(issue));
                if (prunedIssues.size() >= MAX_REFINEMENT_ISSUES) break;
            }
        }
        if (prunedIssues.isEmpty() && issues != null && !issues.isEmpty()) {
            prunedIssues.add(copyIssue(issues.getFirst()));
        }
        return ReviewReport.builder()
                .passed(report.getPassed())
                .score(report.getScore())
                .issues(prunedIssues)
                .summary(truncate(report.getSummary(), MAX_TEXT_CHARS))
                .degraded(report.isDegraded())
                .build();
    }

    private ReviewReport.Issue copyIssue(ReviewReport.Issue issue) {
        return ReviewReport.Issue.builder()
                .severity(issue.getSeverity())
                .file(issue.getFile())
                .description(truncate(issue.getDescription(), MAX_TEXT_CHARS))
                .suggestion(truncate(issue.getSuggestion(), MAX_TEXT_CHARS))
                .build();
    }

    private RefinementPlan pruneRefinementPlan(RefinementPlan plan) {
        if (plan == null) return null;
        return new RefinementPlan(
                limit(plan.targetFiles(), MAX_TARGET_FILES),
                truncate(plan.instructions(), MAX_TEXT_CHARS),
                plan.attemptNumber()
        );
    }

    private <T> List<T> limit(List<T> values, int max) {
        if (values == null) return List.of();
        return values.stream().limit(max).toList();
    }

    private String truncate(String value, int maxChars) {
        if (value == null || value.length() <= maxChars) return value;
        return value.substring(0, maxChars) + "...";
    }

    private void logReduction(String label, Object before, Object after) {
        int beforeChars = estimateChars(before);
        int afterChars = estimateChars(after);
        if (beforeChars <= 0) return;
        double reduction = (beforeChars - afterChars) * 100.0 / beforeChars;
        log.info("Context pruning {}: {} chars -> {} chars ({}% reduction)",
                label, beforeChars, afterChars, String.format("%.1f", reduction));
    }

    private void logCodeGenDiagnostics(CodeGenInput before, CodeGenInput after) {
        if (!contextPruningDiagnosticsEnabled || before == null || after == null) {
            return;
        }
        log.info(
                "Context pruning CodeGenInput fields: "
                        + "requirementSpec {} -> {}, imageListStr {} -> {}, taskGraph {} -> {}, "
                        + "features {} -> {}, constraints {} -> {}, imageLines {} -> {}, "
                        + "codeGenHints {} -> {}, reviewFocusAreas removed={}",
                estimateChars(before.requirementSpec()),
                estimateChars(after.requirementSpec()),
                charLength(before.imageListStr()),
                charLength(after.imageListStr()),
                estimateChars(before.taskGraph()),
                estimateChars(after.taskGraph()),
                size(before.requirementSpec() != null ? before.requirementSpec().getFeatures() : null),
                size(after.requirementSpec() != null ? after.requirementSpec().getFeatures() : null),
                size(before.requirementSpec() != null ? before.requirementSpec().getTechConstraints() : null),
                size(after.requirementSpec() != null ? after.requirementSpec().getTechConstraints() : null),
                lineCount(before.imageListStr()),
                lineCount(after.imageListStr()),
                before.taskGraph() != null ? charLength(before.taskGraph().codeGenHints()) : 0,
                after.taskGraph() != null ? charLength(after.taskGraph().codeGenHints()) : 0,
                before.taskGraph() != null
                        && before.taskGraph().reviewFocusAreas() != null
                        && (after.taskGraph() == null || after.taskGraph().reviewFocusAreas() == null)
        );
    }

    private int estimateChars(Object value) {
        if (value == null) return 0;
        try {
            return Json.toJson(value).length();
        } catch (Exception e) {
            return String.valueOf(value).length();
        }
    }

    private int charLength(String value) {
        return value != null ? value.length() : 0;
    }

    private int lineCount(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        int count = 0;
        for (String line : value.split("\\R")) {
            if (!line.isBlank()) {
                count++;
            }
        }
        return count;
    }

    private int size(List<?> values) {
        return values != null ? values.size() : 0;
    }
}
