package com.ling.lingaicodegeneration.service;

import com.ling.lingaicodegeneration.model.dto.user.UserQueryRequest;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.model.vo.LoginUserVO;
import com.ling.lingaicodegeneration.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的单个用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取加密密码
     */
    String getEncryptPassword(String userPassword);
    /**
     * 获取查询条件
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}