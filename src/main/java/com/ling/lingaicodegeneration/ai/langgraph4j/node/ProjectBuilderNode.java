package com.ling.lingaicodegeneration.ai.langgraph4j.node;

import com.ling.lingaicodegeneration.ai.langgraph4j.state.WorkflowContext;
import com.ling.lingaicodegeneration.core.builder.VueProjectBuilder;
import com.ling.lingaicodegeneration.exception.BusinessException;
import com.ling.lingaicodegeneration.exception.ErrorCode;
import com.ling.lingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ling.lingaicodegeneration.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.state.AgentState;

import java.io.File;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ProjectBuilderNode {

    public static AsyncNodeAction<AgentState> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("Executing node: Project Builder");
            String generatedCodeDir = context.getGeneratedCodeDir();
            CodeGenTypeEnum generationType = context.getGenerationType();
            String buildResultDir;
            // Vue project needs npm build
            if (generationType == CodeGenTypeEnum.VUE_PROJECT) {
                try {
                    VueProjectBuilder vueBuilder =
                            SpringContextUtil.getBean(VueProjectBuilder.class);
                    boolean buildSuccess = vueBuilder.buildProject(generatedCodeDir);
                    if (buildSuccess) {
                        buildResultDir = generatedCodeDir + File.separator + "dist";
                        log.info("Vue project build success, dist: {}", buildResultDir);
                    } else {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                                "Vue project build failed");
                    }
                } catch (BusinessException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Vue project build error: {}", e.getMessage(), e);
                    buildResultDir = generatedCodeDir;
                }
            } else {
                // HTML and MULTI_FILE use generated code dir directly
                buildResultDir = generatedCodeDir;
            }
            context.setCurrentStep("Project Builder");
            context.setBuildResultDir(buildResultDir);
            log.info("Project build completed, result dir: {}", buildResultDir);
            return WorkflowContext.saveContext(context);
        });
    }
}