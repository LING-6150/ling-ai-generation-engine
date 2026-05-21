package com.ling.lingaicodegeneration.ai.multiagent.agent;

import com.ling.lingaicodegeneration.ai.multiagent.llm.RequirementLlmService;
import com.ling.lingaicodegeneration.ai.multiagent.model.Complexity;
import com.ling.lingaicodegeneration.ai.multiagent.model.RequirementSpec;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RequirementAgent implements Agent<String, RequirementSpec> {

    @Resource
    private RequirementLlmService requirementLlmService;

    @Override
    public RequirementSpec execute(String prompt, AgentContext context) {
        log.info("RequirementAgent executing, appId: {}", context.appId());
        try {
            RequirementSpec spec = requirementLlmService.analyze(prompt);
            if (spec.getCodeGenType() == null) {
                spec.setCodeGenType(CodeGenTypeEnum.HTML);
            }
            if (spec.getComplexity() == null) {
                spec.setComplexity(Complexity.MEDIUM);
            }
            if (spec.getFeatures() == null) {
                spec.setFeatures(List.of());
            }
            if (spec.getTechConstraints() == null) {
                spec.setTechConstraints(List.of());
            }
            log.info("RequirementAgent completed: type={}, complexity={}",
                    spec.getCodeGenType(), spec.getComplexity());
            return spec;
        } catch (Exception e) {
            log.error("RequirementAgent failed, falling back to defaults: {}", e.getMessage());
            return RequirementSpec.builder()
                    .codeGenType(CodeGenTypeEnum.HTML)
                    .projectDescription(prompt)
                    .features(List.of())
                    .techConstraints(List.of())
                    .complexity(Complexity.MEDIUM)
                    .build();
        }
    }
}
