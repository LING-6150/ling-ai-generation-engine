package com.ling.lingaicodegeneration.ai.langgraph4j.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Image resource object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResource implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Image description */
    private String description;

    /** Image URL */
    private String url;
}