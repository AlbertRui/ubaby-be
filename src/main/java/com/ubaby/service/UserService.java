package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.User;

/**
 * @author AlbertRui
 * @date 2018-05-04 20:11
 */
public interface UserService {

    /**
     * 用户登录业务接口
     *
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 用户注册业务接口
     *
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 用户名和email校验
     *
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);
}
