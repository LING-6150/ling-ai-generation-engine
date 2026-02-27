package com.ling.lingaicodegeneration.model.dto.app;

import com.ling.lingaicodegeneration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryRequest extends PageRequest implements Serializable {

    private Long id;
    private String appName;
    private String cover;
    private String initPrompt;
    private String codeGenType;
    private String deployKey;
    private Integer priority;
    private Long userId;

    private static final long serialVersionUID = 1L;
}