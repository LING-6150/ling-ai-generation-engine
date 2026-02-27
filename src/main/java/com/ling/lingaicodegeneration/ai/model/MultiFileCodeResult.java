package com.ling.lingaicodegeneration.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("Result of generating multi-file code")
@Data
public class MultiFileCodeResult {

    @Description("The generated HTML code")
    private String htmlCode;

    @Description("The generated CSS code")
    private String cssCode;

    @Description("The generated JavaScript code")
    private String jsCode;

    @Description("A brief description of the generated code")
    private String description;
}