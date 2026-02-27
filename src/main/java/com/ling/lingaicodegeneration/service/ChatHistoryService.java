package com.ling.lingaicodegeneration.service;

import com.mybatisflex.core.paginate.Page;
import com.ling.lingaicodegeneration.model.entity.ChatHistory;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;



public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 从数据库加载对话历史到记忆中
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);


    /**
     * 新增对话历史
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 根据 appId 删除对话历史
     */
    boolean deleteByAppId(Long appId);

    /**
     * 游标分页查询某个应用的对话历史
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               com.ling.lingaicodegeneration.model.entity.User loginUser);
}