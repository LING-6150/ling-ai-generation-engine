package com.ling.lingaicodegeneration.ai.langgraph4j.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import java.util.List;

@Data
@Description("Plan for collecting images")
public class ImageCollectionPlan {

    @Description("List of search keywords for content images, max 3 keywords")
    private List<String> contentImageKeywords;
}