package com.ling.lingaicodegeneration.multiagent.verify;

import com.ling.lingaicodegeneration.ai.multiagent.verify.HtmlRuntimeVerifier;
import com.ling.lingaicodegeneration.ai.multiagent.verify.VerificationReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HtmlRuntimeVerifierTest {

    private final HtmlRuntimeVerifier verifier = new HtmlRuntimeVerifier();

    @Test
    void missingIndex_isActionableFailure_withoutLaunchingBrowser(@TempDir Path dir) {
        VerificationReport report = verifier.verify(dir.toString());

        assertTrue(report.hasActionableFailures());
        assertEquals("missing_entry", report.failures().get(0).type());
    }

    // Real Chromium; opt in with RUN_BROWSER_TESTS=1 (CI has no browser installed).
    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_BROWSER_TESTS", matches = "1")
    void cleanPage_passes(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("index.html"),
                "<html><body><button id='start'>Start</button><script src='script.js'></script></body></html>");
        Files.writeString(dir.resolve("script.js"), "document.getElementById('start').textContent = 'Go';");

        VerificationReport report = verifier.verify(dir.toString());

        assertTrue(report.available(), report.unavailableReason());
        assertTrue(report.passed(), String.valueOf(report.failures()));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_BROWSER_TESTS", matches = "1")
    void siblingScriptError_andMissingResource_areReported(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("index.html"),
                "<html><head><link rel='stylesheet' href='missing.css'></head>"
                        + "<body><script src='script.js'></script></body></html>");
        Files.writeString(dir.resolve("script.js"), "startTimer();");

        VerificationReport report = verifier.verify(dir.toString());

        assertTrue(report.available(), report.unavailableReason());
        assertTrue(report.hasActionableFailures());
        assertTrue(report.failures().stream().anyMatch(f ->
                f.type().equals("page_error") && f.message().contains("startTimer")), String.valueOf(report.failures()));
        assertTrue(report.failures().stream().anyMatch(f ->
                f.type().equals("resource_failed") && f.message().contains("missing.css")), String.valueOf(report.failures()));
    }
}
