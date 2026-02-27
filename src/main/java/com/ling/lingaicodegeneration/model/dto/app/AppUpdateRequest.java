package com.ling.lingaicodegeneration.model.dto.app;

import lombok.Data;
import java.io.Serializable;

@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * App name
     */
    private String appName;

    private static final long serialVersionUID = 1L;
}