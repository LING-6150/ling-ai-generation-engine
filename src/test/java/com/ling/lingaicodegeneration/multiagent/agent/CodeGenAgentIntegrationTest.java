package com.ling.lingaicodegeneration.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.agent.AgentContext;
import com.ling.lingaicodegeneration.ai.multiagent.agent.CodeGenAgent;
import com.ling.lingaicodegeneration.ai.multiagent.model.CodeGenInput;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.ai.multiagent.model.TaskGraph;
import com.ling.lingaicodegeneration.core.CodeFileSaver;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("llm")
@EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".+")
class CodeGenAgentIntegrationTest {

    @Resource
    private CodeGenAgent codeGenAgent;

    @Test
    void execute_htmlType_generatesFileAndFluxCompletes() {
        Long testAppId = 888L;
        RequirementSpec spec = RequirementSpec.builder()
                .codeGenType(CodeGenTypeEnum.HTML)
                .projectDescription("A minimal hello-world page with a heading and one paragraph")
                .features(List.of("display a greeting heading", "one paragraph of text"))
                .techConstraints(List.of("no frameworks, plain HTML and CSS"))
                .complexity(Complexity.SIMPLE)
                .build();
        TaskGraph taskGraph = new TaskGraph(false, 1, null, null);
        CodeGenInput input = new CodeGenInput(spec, "", taskGraph);
        AgentContext ctx = new AgentContext(testAppId, testAppId, 1, "CodeGenAgent");

        Flux<String> flux = codeGenAgent.execute(input, ctx);

        // Collect all tokens; block up to 2 minutes for the real API call
        List<String> tokens = flux.collectList().block(Duration.ofMinutes(2));

        assertNotNull(tokens, "Flux must complete (not timeout)");
        assertFalse(tokens.isEmpty(), "Flux must emit at least one token");

        // Verify the output file was saved to the standard path
        String outputDir = CodeFileSaver.resolveOutputDir(CodeGenTypeEnum.HTML, testAppId);
        File indexHtml = new File(outputDir + File.separator + "index.html");
        assertTrue(indexHtml.exists(), "index.html must be written to " + outputDir);
        assertTrue(indexHtml.length() > 0, "index.html must not be empty");
    }
}
