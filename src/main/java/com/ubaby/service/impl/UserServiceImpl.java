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
        int resultCount = userMapper.checkUsername(user.getUsername());
        if (resultCount > 0)
            return ServerResponse.createByErrorMessage("用户名已存在");
        resultCount = userMapper.checkEmail(user.getEmail());
        if (resultCount > 0)
            return ServerResponse.createByErrorMessage("email已存在");
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        resultCount = userMapper.insert(user);
        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("注册失败");
        return ServerResponse.createBySuccessMessage("注册成功");
    }
}
