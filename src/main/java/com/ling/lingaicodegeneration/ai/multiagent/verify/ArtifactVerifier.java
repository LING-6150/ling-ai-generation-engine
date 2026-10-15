package com.ling.lingaicodegeneration.ai.multiagent.verify;

/**
 * Deterministic check of a generated project directory. No LLM involved.
 */
public interface ArtifactVerifier {

    VerificationReport verify(String outputDir);
}
