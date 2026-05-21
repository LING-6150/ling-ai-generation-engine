package com.ling.lingaicodegeneration.ai.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.llm.RefineAiService;
import com.ling.lingaicodegeneration.ai.multiagent.model.RefineInput;
import com.ling.lingaicodegeneration.ai.tools.FileDirReadTool;
import com.ling.lingaicodegeneration.ai.tools.FileModifyTool;
import com.ling.lingaicodegeneration.ai.tools.FileReadTool;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.internal.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Component
public class RefineAgent implements Agent<RefineInput, Flux<String>> {

    @Override
    public Flux<String> execute(RefineInput input, AgentContext ctx) {
        log.info("RefineAgent executing, appId: {}, attempt: {}/{}",
                ctx.appId(), input.refinementPlan().attemptNumber(), ctx.retryBudget());
        try {
            OpenAiStreamingChatModel model = SpringContextUtil.getBean(
                    "reasoningStreamingChatModel", OpenAiStreamingChatModel.class);

            String generatedCodeDir = input.generatedCodeDir();
            MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .id(ctx.appId())
                    .maxMessages(30)
                    .build();

            RefineAiService refineService = AiServices.builder(RefineAiService.class)
                    .streamingChatModel(model)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(
                            new FileDirReadTool(generatedCodeDir),
                            new FileReadTool(generatedCodeDir),
                            new FileModifyTool(generatedCodeDir)
                    )
                    .hallucinatedToolNameStrategy(req ->
                            ToolExecutionResultMessage.from(req,
                                    "Error: there is no tool called " + req.name()))
                    .maxSequentialToolsInvocations(20)
                    .build();

            TokenStream tokenStream = refineService.refine(buildRefinementRequest(input));

            return Flux.create(sink -> tokenStream
                    .onPartialResponse(sink::next)
                    .onToolExecuted(exec -> log.info("RefineAgent tool: {} args: {}",
                            exec.request().name(), exec.request().arguments()))
                    .onCompleteResponse(response -> {
                        log.info("RefineAgent completed, appId: {}", ctx.appId());
                        sink.complete();
                    })
                    .onError(error -> sink.error(new RuntimeException(error.getMessage())))
                    .start());

        } catch (Exception e) {
            log.error("RefineAgent failed: {}", e.getMessage(), e);
            return Flux.error(e);
        }
    }

    // Visible for unit testing
    public String buildRefinementRequest(RefineInput input) {
        String reviewJson = Json.toJson(input.reviewReport());
        List<String> targetFiles = input.refinementPlan().targetFiles();
        String instructions = input.refinementPlan().instructions();

        return String.format("""
                Code directory: %s
                Attempt: %d of 3

                Review Report JSON:
                %s

                Additional instructions: %s

                Focus on these files: %s

                Start by listing the directory structure, then read the relevant files, \
                then apply fixes using the Modify File tool.
                """,
                input.generatedCodeDir(),
                input.refinementPlan().attemptNumber(),
                reviewJson,
                instructions != null ? instructions : "Follow the review report",
                targetFiles == null || targetFiles.isEmpty()
                        ? "all files in the project"
                        : String.join(", ", targetFiles)
        );
    }
}
