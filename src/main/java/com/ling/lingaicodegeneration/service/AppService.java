package com.ling.lingaicodegeneration.service;

import com.ling.lingaicodegeneration.model.dto.app.AppQueryRequest;
import com.ling.lingaicodegeneration.model.entity.App;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AppService extends IService<App> {

    /**
     * Get query wrapper
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * Get app VO
     */
    AppVO getAppVO(App app);

    /**
     * Get app VO list (batch, avoids N+1 query)
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * Deploy app
     */
    String deployApp(Long appId, User loginUser);

    /**
     * Chat to generate code (streaming)
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);
}