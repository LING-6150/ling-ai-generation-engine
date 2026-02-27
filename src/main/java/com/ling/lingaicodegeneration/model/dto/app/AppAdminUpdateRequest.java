package com.ling.lingaicodegeneration.model.dto.app;

import lombok.Data;
import java.io.Serializable;

@Data
public class AppAdminUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * App name
     */
    private String appName;

    /**
     * Cover image
     */
    private String cover;

    /**
     * Priority
     */
    private Integer priority;

    private static final long serialVersionUID = 1L;
}