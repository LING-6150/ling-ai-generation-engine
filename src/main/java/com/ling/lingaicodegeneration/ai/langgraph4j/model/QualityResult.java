package com.ling.lingaicodegeneration.ai.langgraph4j.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Code quality check result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Whether code passed quality check */
    private Boolean isValid;

    /** List of errors found */
    private List<String> errors;

    /** List of improvement suggestions */
    private List<String> suggestions;
}