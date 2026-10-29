package com.ling.lingaicodegeneration.multiagent.verify;

import com.ling.lingaicodegeneration.ai.multiagent.verify.VerificationReport;
import com.ling.lingaicodegeneration.ai.multiagent.verify.VueBuildVerifier;
import com.ling.lingaicodegeneration.core.builder.VueBuildResult;
import com.ling.lingaicodegeneration.core.builder.VueProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VueBuildVerifierTest {

    @Mock private VueProjectBuilder vueProjectBuilder;
    @InjectMocks private VueBuildVerifier verifier;

    @Test
    void passedBuild_isPassedReport() {
        when(vueProjectBuilder.buildProjectWithResult("/out/vue"))
                .thenReturn(VueBuildResult.passed(1200, "built", ""));

        VerificationReport report = verifier.verify("/out/vue");

        assertTrue(report.available());
        assertTrue(report.passed());
        assertEquals(1200, report.durationMillis());
    }

    @Test
    void failedBuild_surfacesTailOfErrorOutput() {
        String error = "x".repeat(3000) + "\n[vite] src/App.vue: Unexpected token";
        when(vueProjectBuilder.buildProjectWithResult("/out/vue"))
                .thenReturn(VueBuildResult.failed(800, "npm output", error));

        VerificationReport report = verifier.verify("/out/vue");

        assertTrue(report.hasActionableFailures());
        assertEquals("build_failure", report.failures().get(0).type());
        assertTrue(report.failures().get(0).message().endsWith("Unexpected token"));
        assertTrue(report.failures().get(0).message().length() <= 1503);
    }

    @Test
    void failedBuildWithoutStderr_fallsBackToStdout() {
        when(vueProjectBuilder.buildProjectWithResult("/out/vue"))
                .thenReturn(VueBuildResult.failed(800, "npm ERR! missing script: build", ""));

        VerificationReport report = verifier.verify("/out/vue");

        assertEquals("npm ERR! missing script: build", report.failures().get(0).message());
    }
}
