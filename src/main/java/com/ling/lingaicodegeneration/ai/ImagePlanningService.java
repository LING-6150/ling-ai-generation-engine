package com.ling.lingaicodegeneration.ai;

import com.ling.lingaicodegeneration.ai.langgraph4j.model.ImageCollectionPlan;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ImagePlanningService {

    @SystemMessage("You are an image search planner. Based on the user's website description, " +
            "generate 2-3 specific English search keywords for finding relevant content images. " +
            "Return JSON only, no other text.")
    ImageCollectionPlan planImageCollection(@UserMessage String userPrompt);
}