package com.ubaby.service;

import com.ubaby.common.ServerResponse;

import java.util.Map;

/**
 * @author AlbertRui
 * @date 2018-05-12 18:26
 */
@SuppressWarnings("JavaDoc")
public interface OrderService {

    /**
     * 订单操作
     *
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    ServerResponse<Map<String, String>> pay(Long orderNo, Integer userId, String path);
}
