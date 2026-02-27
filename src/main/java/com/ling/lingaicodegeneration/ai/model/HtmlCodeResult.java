package com.ling.lingaicodegeneration.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("Result of generating a single HTML file")
@Data
public class HtmlCodeResult {

    @Description("The generated HTML code")
    private String htmlCode;

    @Description("A brief description of the generated code")
    private String description;
}