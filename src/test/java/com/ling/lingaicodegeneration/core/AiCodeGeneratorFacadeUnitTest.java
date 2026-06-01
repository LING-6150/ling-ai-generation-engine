package com.ling.lingaicodegeneration.core;

import com.ling.lingaicodegeneration.ai.AiCodeGeneratorService;
import com.ling.lingaicodegeneration.ai.AiCodeGeneratorServiceFactory;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiCodeGeneratorFacadeUnitTest {

    @Mock
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    @Mock
    private AiCodeGeneratorService aiCodeGeneratorService;

    private AiCodeGeneratorFacade facade;

    @BeforeEach
    void setUp() {
        facade = new AiCodeGeneratorFacade();
        ReflectionTestUtils.setField(facade, "aiCodeGeneratorServiceFactory", aiCodeGeneratorServiceFactory);
    }

    @Test
    void generateAndSaveCodeStream_emptyMultiFileStream_failsFast() {
        when(aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(123L, CodeGenTypeEnum.MULTI_FILE))
                .thenReturn(aiCodeGeneratorService);
        when(aiCodeGeneratorService.generateMultiFileCodeStream("prompt"))
                .thenReturn(Flux.empty());

        assertThrows(IllegalStateException.class, () ->
                facade.generateAndSaveCodeStream("prompt", CodeGenTypeEnum.MULTI_FILE, 123L)
                        .collectList()
                        .block(Duration.ofSeconds(5))
        );
    }

    @Test
    void generateAndSaveCodeStream_unparseableMultiFileStream_failsFast() {
        when(aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(123L, CodeGenTypeEnum.MULTI_FILE))
                .thenReturn(aiCodeGeneratorService);
        when(aiCodeGeneratorService.generateMultiFileCodeStream("prompt"))
                .thenReturn(Flux.just("plain text without code fences"));

        assertThrows(IllegalStateException.class, () ->
                facade.generateAndSaveCodeStream("prompt", CodeGenTypeEnum.MULTI_FILE, 123L)
                        .collectList()
                        .block(Duration.ofSeconds(5))
        );
    }
}
