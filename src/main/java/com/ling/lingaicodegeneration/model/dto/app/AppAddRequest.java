package com.ling.lingaicodegeneration.model.dto.app;

import lombok.Data;
import java.io.Serializable;

@Data
public class AppAddRequest implements Serializable {

    /**
     * Initial prompt
     */
    private String initPrompt;

    /**
     * Code generation type: html, multi_file, vue_project
     */
    private String codeGenType;

    private static final long serialVersionUID = 1L;
}