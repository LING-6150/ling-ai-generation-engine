package com.ling.lingaicodegeneration.multiagent.verify;

import com.ling.lingaicodegeneration.ai.multiagent.verify.ExecutionVerifier;
import com.ling.lingaicodegeneration.ai.multiagent.verify.HtmlRuntimeVerifier;
import com.ling.lingaicodegeneration.ai.multiagent.verify.VerificationReport;
import com.ling.lingaicodegeneration.ai.multiagent.verify.VueBuildVerifier;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionVerifierTest {

    @Mock private HtmlRuntimeVerifier htmlRuntimeVerifier;
    @Mock private VueBuildVerifier vueBuildVerifier;
    @InjectMocks private ExecutionVerifier executionVerifier;

    @Test
    void htmlAndMultiFile_useBrowserVerifier_vueUsesBuild() {
        when(htmlRuntimeVerifier.verify(anyString())).thenReturn(VerificationReport.passed(1));
        when(vueBuildVerifier.verify(anyString())).thenReturn(VerificationReport.passed(2));

        executionVerifier.verify(CodeGenTypeEnum.HTML, "/out/html_1");
        executionVerifier.verify(CodeGenTypeEnum.MULTI_FILE, "/out/multi_file_1");
        executionVerifier.verify(CodeGenTypeEnum.VUE_PROJECT, "/out/vue_project_1");

        verify(htmlRuntimeVerifier).verify("/out/html_1");
        verify(htmlRuntimeVerifier).verify("/out/multi_file_1");
        verify(vueBuildVerifier).verify("/out/vue_project_1");
    }

    @Test
    void verifierCrash_becomesUnavailable_neverThrows() {
        when(htmlRuntimeVerifier.verify(anyString())).thenThrow(new IllegalStateException("driver crashed"));

        VerificationReport report = executionVerifier.verify(CodeGenTypeEnum.HTML, "/out/html_1");

        assertFalse(report.available());
        assertFalse(report.hasActionableFailures());
        assertTrue(report.unavailableReason().contains("driver crashed"));
    }

    @Test
    void nullType_isUnavailable() {
        assertFalse(executionVerifier.verify(null, "/out").available());
        verifyNoInteractions(htmlRuntimeVerifier, vueBuildVerifier);
    }
}
