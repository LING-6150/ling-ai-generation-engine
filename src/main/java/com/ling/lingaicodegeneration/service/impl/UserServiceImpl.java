package com.ling.lingaicodegeneration.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ling.lingaicodegeneration.model.entity.User;
import com.ling.lingaicodegeneration.mapper.UserMapper;
import com.ling.lingaicodegeneration.service.UserService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
