package com.ling.lingaicodegeneration.ai.multiagent.model;

import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementSpec {

    @Description("The recommended code generation type based on project complexity")
    private CodeGenTypeEnum codeGenType;

    @Description("A concise one-sentence description of the project")
    private String projectDescription;

    @Description("List of key features the generated code should implement")
    private List<String> features;

    @Description("Technical constraints or preferences mentioned by the user")
    private List<String> techConstraints;

    @Description("Estimated complexity of the project")
    private Complexity complexity;
}
