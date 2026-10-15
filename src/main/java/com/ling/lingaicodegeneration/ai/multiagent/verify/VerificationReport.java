package com.ling.lingaicodegeneration.ai.multiagent.verify;

import java.util.List;

/**
 * Result of actually loading or building the generated project.
 * <p>
 * Generic runtime health only (load errors, console/page errors, build failures).
 * It never sees eval-case checks, so the generator cannot learn the judge's rules.
 *
 * @param available false when the verifier itself could not run (e.g. no browser);
 *                  the orchestrator then falls back to review-only behavior
 */
public record VerificationReport(
        boolean available,
        boolean passed,
        List<Failure> failures,
        long durationMillis,
        String unavailableReason
) {

    public record Failure(String type, String message) {}

    public static VerificationReport passed(long durationMillis) {
        return new VerificationReport(true, true, List.of(), durationMillis, null);
    }

    public static VerificationReport failed(List<Failure> failures, long durationMillis) {
        return new VerificationReport(true, false, List.copyOf(failures), durationMillis, null);
    }

    public static VerificationReport unavailable(String reason, long durationMillis) {
        return new VerificationReport(false, false, List.of(), durationMillis, reason);
    }

    /** True only when the verifier ran and found real failures worth feeding to Refine. */
    public boolean hasActionableFailures() {
        return available && !passed && !failures.isEmpty();
    }
}
