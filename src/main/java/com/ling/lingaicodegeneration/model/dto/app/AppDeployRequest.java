package com.ling.lingaicodegeneration.model.dto.app;

import lombok.Data;
import java.io.Serializable;

@Data
public class AppDeployRequest implements Serializable {

    /**
     * App id
     */
    private Long appId;

    private static final long serialVersionUID = 1L;
}