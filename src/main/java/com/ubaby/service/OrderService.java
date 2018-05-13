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
     * 支付操作
     *
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    ServerResponse<Map<String, String>> pay(Long orderNo, Integer userId, String path);

    /**
     * 支付宝回调
     *
     * @param params
     * @return
     */
    ServerResponse<String> alipayCallBack(Map<String, String> params);

    /**
     * 查询订单状态
     *
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);
}
