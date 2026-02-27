package com.ling.lingaicodegeneration.ai;

import com.ling.lingaicodegeneration.ai.model.HtmlCodeResult;
import com.ling.lingaicodegeneration.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("A simple todo list app");
        System.out.println("HTML Code: " + result.getHtmlCode());
        System.out.println("Description: " + result.getDescription());
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("A simple todo list app");
        System.out.println("HTML: " + result.getHtmlCode());
        System.out.println("CSS: " + result.getCssCode());
        System.out.println("JS: " + result.getJsCode());
        System.out.println("Description: " + result.getDescription());
    }
}