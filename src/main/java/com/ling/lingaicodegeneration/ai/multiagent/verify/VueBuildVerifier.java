package com.ling.lingaicodegeneration.ai.multiagent.verify;

import com.ling.lingaicodegeneration.core.builder.VueBuildResult;
import com.ling.lingaicodegeneration.core.builder.VueProjectBuilder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs npm install + npm run build and surfaces the build error output.
 * Rebuilding on every attempt also keeps dist/ in sync with RefineAgent's edits.
 */
@Component
public class VueBuildVerifier implements ArtifactVerifier {

    static final int MAX_ERROR_CHARS = 1500;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Override
    public VerificationReport verify(String outputDir) {
        VueBuildResult build = vueProjectBuilder.buildProjectWithResult(outputDir);
        if (build.passed()) {
            return VerificationReport.passed(build.durationMillis());
        }
        String output = build.errorSnippet().isBlank() ? build.outputSnippet() : build.errorSnippet();
        return VerificationReport.failed(
                List.of(new VerificationReport.Failure("build_failure", tail(output))),
                build.durationMillis());
    }

    /** Build tools print the actual error last. */
    private static String tail(String text) {
        if (text == null || text.isBlank()) return "npm build failed without output";
        return text.length() <= MAX_ERROR_CHARS ? text : "..." + text.substring(text.length() - MAX_ERROR_CHARS);
    }
}
