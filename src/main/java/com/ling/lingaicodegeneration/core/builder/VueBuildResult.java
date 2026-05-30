package com.ling.lingaicodegeneration.core.builder;

/**
 * Stable result representation for a Vue project build.
 */
public record VueBuildResult(
        boolean passed,
        long durationMillis,
        String outputSnippet,
        String errorSnippet
) {

    public static VueBuildResult passed(long durationMillis, String outputSnippet, String errorSnippet) {
        return new VueBuildResult(true, durationMillis, safeSnippet(outputSnippet), safeSnippet(errorSnippet));
    }

    public static VueBuildResult failed(long durationMillis, String outputSnippet, String errorSnippet) {
        return new VueBuildResult(false, durationMillis, safeSnippet(outputSnippet), safeSnippet(errorSnippet));
    }

    private static String safeSnippet(String snippet) {
        return snippet == null ? "" : snippet;
    }
}
