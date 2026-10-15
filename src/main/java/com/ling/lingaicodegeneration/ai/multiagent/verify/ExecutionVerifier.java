package com.ling.lingaicodegeneration.ai.multiagent.verify;

import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Picks the deterministic verifier for a code type. Any verifier crash becomes
 * "unavailable" so execution feedback can never break generation.
 */
@Slf4j
@Component
public class ExecutionVerifier {

    @Resource
    private HtmlRuntimeVerifier htmlRuntimeVerifier;

    @Resource
    private VueBuildVerifier vueBuildVerifier;

    public VerificationReport verify(CodeGenTypeEnum type, String outputDir) {
        long start = System.nanoTime();
        try {
            if (type == null) {
                return VerificationReport.unavailable("unknown code type", 0);
            }
            return switch (type) {
                case HTML, MULTI_FILE -> htmlRuntimeVerifier.verify(outputDir);
                case VUE_PROJECT -> vueBuildVerifier.verify(outputDir);
            };
        } catch (Exception e) {
            log.warn("Execution verification failed to run for {}: {}", outputDir, e.getMessage());
            return VerificationReport.unavailable(
                    e.getClass().getSimpleName() + ": " + e.getMessage(),
                    (System.nanoTime() - start) / 1_000_000);
        }
    }
}
