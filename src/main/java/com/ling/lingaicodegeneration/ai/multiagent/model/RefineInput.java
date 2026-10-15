package com.ling.lingaicodegeneration.ai.multiagent.model;

import com.ling.lingaicodegeneration.ai.multiagent.verify.VerificationReport;

/**
 * @param verificationReport real load/build failures to fix first; null when
 *                           execution feedback is off or found nothing actionable
 */
public record RefineInput(
        String generatedCodeDir,
        ReviewReport reviewReport,
        RefinementPlan refinementPlan,
        VerificationReport verificationReport
) {
    public RefineInput(String generatedCodeDir, ReviewReport reviewReport, RefinementPlan refinementPlan) {
        this(generatedCodeDir, reviewReport, refinementPlan, null);
    }
}
