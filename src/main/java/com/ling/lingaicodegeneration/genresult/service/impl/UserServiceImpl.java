package com.ling.lingaicodegeneration.genresult.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ling.lingaicodegeneration.genresult.entity.User;
import com.ling.lingaicodegeneration.genresult.mapper.UserMapper;
import com.ling.lingaicodegeneration.genresult.service.UserService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
