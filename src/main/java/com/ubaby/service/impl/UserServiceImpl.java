package com.ubaby.service.impl;

import com.ubaby.common.Const;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.UserMapper;
import com.ubaby.pojo.User;
import com.ubaby.service.UserService;
import com.ubaby.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author AlbertRui
 * @date 2018-05-04 20:13
 */
@SuppressWarnings("JavaDoc")
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("用户名不存在");
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null)
            return ServerResponse.createByErrorMessage("密码错误");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess())
            return validResponse;
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess())
            return validResponse;

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("注册失败");
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0)
                    return ServerResponse.createByErrorMessage("用户名已存在");
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0)
                    return ServerResponse.createByErrorMessage("email已存在");
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 密码提示接口
     *
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess())
            return ServerResponse.createByErrorMessage("用户不存在");//也就是调用checkValid方法校验成功
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question))
            return ServerResponse.createBySuccess(question);
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }
}
