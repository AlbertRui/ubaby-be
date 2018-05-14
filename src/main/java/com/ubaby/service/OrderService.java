package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.OrderItem;
import com.ubaby.vo.OrderProduct;

import java.util.List;
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

    /**
     * 创建订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    ServerResponse create(Integer userId, Integer shippingId);

    /**
     * 删除订单
     *
     * @param userId
     * @param orderNo
     * @return
     */
    ServerResponse<String> cancel(Integer userId, Long orderNo);

    /**
     * 获取购物车中已经选中的商品
     *
     * @param userId
     * @return
     */
    ServerResponse getOrderCartProduct(Integer userId);
}
