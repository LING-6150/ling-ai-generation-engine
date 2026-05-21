package com.ling.lingaicodegeneration.ai.multiagent.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReport {

    @Description("Whether the code passed the review")
    private Boolean passed;

    @Description("Overall quality score from 0 to 100")
    private Integer score;

    @Description("List of issues found during review")
    private List<Issue> issues;

    @Description("Brief summary of the review result")
    private String summary;

    public enum Severity {
        BLOCKER, MAJOR, MINOR
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Issue {

        @Description("Severity of the issue: BLOCKER, MAJOR, or MINOR")
        private Severity severity;

        @Description("Relative file path where the issue was found, null for project-wide issues")
        private String file;

        @Description("Description of the issue")
        private String description;

        @Description("Suggested fix for the issue")
        private String suggestion;
    }
}
