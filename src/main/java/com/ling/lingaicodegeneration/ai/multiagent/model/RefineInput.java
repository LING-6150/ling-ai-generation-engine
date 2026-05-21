package com.ling.lingaicodegeneration.ai.multiagent.model;

public record RefineInput(
        String generatedCodeDir,
        ReviewReport reviewReport,
        RefinementPlan refinementPlan
) {}
