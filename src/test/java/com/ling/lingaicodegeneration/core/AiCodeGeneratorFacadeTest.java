package com.ling.lingaicodegeneration.core;

import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode(
                "A task tracker website",
                CodeGenTypeEnum.MULTI_FILE
        );
        assertNotNull(file);
        System.out.println("Saved to: " + file.getAbsolutePath());
    }
    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(
                "A task tracker website",
                CodeGenTypeEnum.MULTI_FILE
        );
        // collect all chunks and block until complete
        List<String> result = codeStream.collectList().block();
        assertNotNull(result);
        String completeContent = String.join("", result);
        System.out.println("Stream complete, total length: " + completeContent.length());
        System.out.println("First 200 chars: " + completeContent.substring(0, Math.min(200, completeContent.length())));
    }
}