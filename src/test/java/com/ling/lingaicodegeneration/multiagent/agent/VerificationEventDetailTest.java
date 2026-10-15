package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.OrchestratorAgent;
import com.ling.lingaicodegeneration.ai.multiagent.verify.VerificationReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VerificationEventDetailTest {

    private static final VerificationReport PAGE_ERROR = VerificationReport.failed(
            List.of(new VerificationReport.Failure("page_error", "startTimer is not defined")), 30);

    @Test
    void describeVerification_summarizesFirstFailure() {
        assertEquals("passed=false,failures=1,ms=30,first=page_error: startTimer is not defined",
                OrchestratorAgent.describeVerification(PAGE_ERROR));
        assertEquals("passed=true,ms=25", OrchestratorAgent.describeVerification(VerificationReport.passed(25)));
        assertTrue(OrchestratorAgent.describeVerification(VerificationReport.unavailable("no browser", 1))
                .startsWith("unavailable="));
    }
}
