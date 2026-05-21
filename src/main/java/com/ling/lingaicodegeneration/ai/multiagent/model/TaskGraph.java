package com.ling.lingaicodegeneration.ai.multiagent.model;

public record TaskGraph(
        boolean parallelizeAsset,
        int suggestedRetries,
        String codeGenHints,
        String reviewFocusAreas
) {}
