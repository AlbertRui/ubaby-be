package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.User;
import org.springframework.stereotype.Service;

/**
 * @author AlbertRui
 * @date 2018-05-04 20:11
 */
@Service
public interface UserService {
    ServerResponse<User> login(String username, String password);
}
