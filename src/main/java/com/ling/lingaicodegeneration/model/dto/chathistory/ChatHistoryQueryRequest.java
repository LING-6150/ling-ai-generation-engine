package com.ling.lingaicodegeneration.model.dto.chathistory;

import com.ling.lingaicodegeneration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * Message content
     */
    private String message;

    /**
     * Message type (user/ai)
     */
    private String messageType;

    /**
     * App id
     */
    private Long appId;

    /**
     * Creator user id
     */
    private Long userId;

    /**
     * Cursor query - last record's create time
     * Used for cursor-based pagination
     */
    private LocalDateTime lastCreateTime;

    private static final long serialVersionUID = 1L;
}