package com.ling.lingaicodegeneration.ai.multiagent.model;

import java.util.List;

public record RefinementPlan(
        List<String> targetFiles,
        String instructions,
        int attemptNumber
) {}
