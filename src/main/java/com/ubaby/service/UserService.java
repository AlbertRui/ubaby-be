package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.User;

/**
 * @author AlbertRui
 * @date 2018-05-04 20:11
 */
@SuppressWarnings("JavaDoc")
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

    /**
     * 密码提示接口
     *
     * @param username
     * @return
     */
    ServerResponse<String> selectQuestion(String username);

    /**
     * 校验密码提示问题
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

    /**
     * 忘记密码后重置密码接口
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew);

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    ServerResponse<User> updateUserInfo(User user);

    /**
     * 获取用户详细信息
     *
     * @param userId
     * @return
     */
    ServerResponse<User> getUserDetails(Integer userId);

    /*============================backend=================================*/

    /**
     * 校验是否是管理员
     *
     * @param user
     * @return
     */
    ServerResponse<String> checkAdminRole(User user);

}
