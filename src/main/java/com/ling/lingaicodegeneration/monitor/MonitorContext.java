package com.ling.lingaicodegeneration.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Monitor context data object.
 * Carries user and app info through the monitoring pipeline.
 * Stored in ThreadLocal and also in ChatModel request attributes
 * to handle cross-thread scenarios in reactive/streaming code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorContext implements Serializable {

    private String userId;
    private String appId;

    @Serial
    private static final long serialVersionUID = 1L;
}